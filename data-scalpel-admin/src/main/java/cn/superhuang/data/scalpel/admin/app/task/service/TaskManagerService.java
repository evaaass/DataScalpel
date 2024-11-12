package cn.superhuang.data.scalpel.admin.app.task.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.actuator.canvas.executor.TaskExecutor;
import cn.superhuang.data.scalpel.admin.app.common.service.KafkaService;
import cn.superhuang.data.scalpel.admin.app.common.service.S3Service;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;

import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstanceLog;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceLogRepository;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceRepository;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskRepository;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceTriggerCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.executor.TaskBaseExecutor;
import cn.superhuang.data.scalpel.admin.app.task.service.interceptor.TaskSubmitInterceptor;
import cn.superhuang.data.scalpel.app.constant.KafkaTopic;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.definition.BatchCanvasTaskDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
public class TaskManagerService {

    @Resource
    private TaskRepository taskRepository;
    @Resource
    private TaskInstanceRepository instanceRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private TaskInstanceLogRepository taskInstanceLogRepository;
    @Resource
    private List<TaskBaseExecutor> taskExecutors;
    @Resource
    private S3Service s3Service;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    public synchronized void submitTask(String taskId, Date scheduledTriggerTime) throws Exception {
        taskRepository.findById(taskId).ifPresentOrElse(task -> {
            if (TaskInstanceExecutionStatus.isRunningStatus(task.getTaskLastRunStatus())) {
                throw new RuntimeException("任务正在运行中");
            }

            task.setTaskLastRunStatus(TaskInstanceExecutionStatus.QUEUING);
            taskRepository.save(task);

            TaskInstance instance = new TaskInstance();
            instance.setTaskId(task.getId());
            instance.setStartTime(new Date());
            instance.setScheduledTriggerTime(scheduledTriggerTime);
            instance.setStatus(TaskInstanceExecutionStatus.QUEUING);
            instance = instanceRepository.save(instance);
            try {
                executeTask(task, instance);
            } catch (Exception e) {
                e.printStackTrace();
                instance.setStatus(TaskInstanceExecutionStatus.FAILURE);
                instanceRepository.save(instance);
                TaskInstanceLog log = TaskInstanceLog.builder().taskId(task.getId()).taskInstanceId(instance.getId()).level(LogLevel.ERROR).message("提交任务失败：%s".formatted(StrUtil.sub(e.getMessage(), 0, 2000))).detail(ExceptionUtil.stacktraceToString(e, 5000)).build();
                taskInstanceLogRepository.save(log);
            }

        }, () -> {
            throw new RuntimeException("任务不存在");
        });
    }

    public void killTask(String taskId) throws Exception {
        taskRepository.findById(taskId).ifPresentOrElse(task -> {
            List<TaskInstance> instances = taskInstanceRepository.findAllByTaskId(taskId);
            instances.forEach(instance -> {
                for (TaskBaseExecutor taskExecutor : taskExecutors) {
                    if (taskExecutor.support(task.getTaskType())) {
                        taskExecutor.kill(instance.getChannelId());
                    }
                }
            });
        }, () -> {
            throw new RuntimeException("任务不存在");
        });

    }

    private void executeTask(Task task, TaskInstance taskInstance) throws Exception {
        for (TaskBaseExecutor taskExecutor : taskExecutors) {
            if (taskExecutor.support(task.getTaskType())) {
                taskExecutor.execute(task, taskInstance);
            }
        }
    }

    //TODO 实例状态改变的时候发个事件，订阅事件改变任务状态吧
    @EventListener
    public void handlerTaskTriggerCompleted(TaskInstanceTriggerCompleteEvent event) {
        TaskTriggerResult taskTriggerResult = event.getTaskTriggerResult();
        taskRepository.findById(taskTriggerResult.getTaskId()).ifPresent(task -> {

            instanceRepository.findById(taskTriggerResult.getTaskInstanceId()).ifPresent(instance -> {
                if (taskTriggerResult.getSuccess() == false) {
                    task.setTaskLastRunStatus(TaskInstanceExecutionStatus.FAILURE);
                    instance.setStatus(TaskInstanceExecutionStatus.FAILURE);
                } else {
                    task.setTaskLastRunStatus(TaskInstanceExecutionStatus.RUNNING);
                    instance.setStatus(TaskInstanceExecutionStatus.RUNNING);
                }
                taskRepository.save(task);
                instanceRepository.save(instance);
            });
        });
    }


    @KafkaListener(topics = {"data_scalpel_task_result"})
    public void consumeTaskResult(String taskResultContent) {
        try {
            TaskResult taskResult = objectMapper.readValue(taskResultContent, TaskResult.class);
            applicationEventPublisher.publishEvent(new TaskInstanceCompleteEvent(this, taskResult));
        } catch (Exception e) {
            log.error("处理任务结果失败:" + e.getMessage(), e);
        }
    }


    private void handleTaskInstanceCompleted(TaskResult taskResult) {
        taskRepository.findById(taskResult.getTaskId()).ifPresent(task -> {
            if (taskResult.getSuccess() == true) {
                task.setSuccessCount(task.getSuccessCount() + 1);
                task.setTaskLastRunStatus(TaskInstanceExecutionStatus.SUCCESS);
            } else {
                task.setFailureCount(task.getFailureCount() + 1);
                task.setTaskLastRunStatus(TaskInstanceExecutionStatus.FAILURE);
            }
            taskRepository.save(task);

            taskInstanceRepository.findById(taskResult.getTaskInstanceId()).ifPresent(taskInstance -> {
                if (taskResult.getSuccess() == true) {
                    taskInstance.setStatus(TaskInstanceExecutionStatus.SUCCESS);
                } else {
                    taskInstance.setStatus(TaskInstanceExecutionStatus.FAILURE);
                }
                taskInstance.setEndTime(new Date());
                taskInstanceRepository.save(taskInstance);
            });
        });
    }


    @EventListener
    public void handlerTaskInstanceCompletedEvent(TaskInstanceCompleteEvent taskInstanceCompleteEvent) {
        try {
            TaskResult taskResult = taskInstanceCompleteEvent.getTaskResult();
            handleTaskInstanceCompleted(taskResult);
        } catch (Exception e) {
            log.error("归档日志到S3失败：" + e.getMessage(), e);
        }
    }


}
