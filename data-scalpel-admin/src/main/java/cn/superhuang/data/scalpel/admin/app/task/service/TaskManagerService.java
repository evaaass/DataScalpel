package cn.superhuang.data.scalpel.admin.app.task.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.superhuang.data.scalpel.admin.app.common.service.KafkaService;
import cn.superhuang.data.scalpel.admin.app.common.service.S3Service;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceRepository;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskRepository;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceTriggerCompleteEvent;
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
public class TaskManagerService implements InitializingBean {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private TaskInstanceRepository instanceRepository;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private S3Service s3Service;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;


    private KafkaConsumer<String, String> batchConsumer;
    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    public synchronized void submitTask(String taskId, Date scheduledTriggerTime) throws Exception {
        taskRepository.findById(taskId).ifPresentOrElse(task -> {
            if (TaskInstanceExecutionStatus.isRunningStatus(task.getTaskLastRunStatus())) {
                throw new RuntimeException("任务正在运行中");
            }
            try {
                task.setTaskLastRunStatus(TaskInstanceExecutionStatus.QUEUING);
                taskRepository.save(task);

                TaskInstance instance = new TaskInstance();
                instance.setTaskId(task.getId());
                instance.setStartTime(new Date());
                //TODO 这个从哪里补呢
                instance.setScheduledTriggerTime(scheduledTriggerTime);
                instance.setStatus(TaskInstanceExecutionStatus.QUEUING);
                instance = instanceRepository.save(instance);

                BatchCanvasTaskDefinition taskDefinition = objectMapper.readValue(task.getDefinition(), BatchCanvasTaskDefinition.class);

                //TODO 这个放到配置文件了里面去

            } catch (Exception e) {
                throw new RuntimeException("提交任务失败：" + e.getMessage(), e);
            }

        }, () -> {
            throw new RuntimeException("任务不存在");
        });

    }

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

    @KafkaListener(topics = {"data_scalpel_trigger_result"})
    public void consumeTaskTriggerResult(String taskTriggerResultContent) {
        try {
            TaskTriggerResult result = objectMapper.readValue(taskTriggerResultContent, TaskTriggerResult.class);
            applicationEventPublisher.publishEvent(new TaskInstanceTriggerCompleteEvent(this, result));
        } catch (Exception e) {
            log.error("处理任务结果失败:" + e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelay = 5 * 1000)
    private void consumeTaskLog() {
        try {
            ConsumerRecords<String, String> records = batchConsumer.poll(Duration.ofMillis(5 * 1000)); // poll间隔1秒
            Map<String, List<String>> logMap = new HashMap<>();
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                String logContent = record.value();
                try {
                    TaskLog log = objectMapper.readValue(logContent, TaskLog.class);
                    String key = getTmpTaskRelLogFilePath(log.getTaskId(), log.getTaskInstanceId());
                    if (!logMap.containsKey(key)) {
                        logMap.put(key, new ArrayList<>());
                    }
                    logMap.get(key).add(objectMapper.writeValueAsString(log));
                } catch (Exception e) {
                    log.error("解析日志失败：" + logContent, e.getMessage());
                }
            }
            // 手动提交偏移量
            batchConsumer.commitSync();
            for (String relFilePath : logMap.keySet()) {
                String logFilePath = FileUtil.getTmpDirPath() + relFilePath;
                FileUtil.appendUtf8Lines(logMap.get(relFilePath), logFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析日志失败,error:" + e.getMessage());
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

    //TODO 以后系统归档记录日志，是否成功失败。。
    private void archiveTaskLogToS3(String taskId, String instanceId) throws Exception {
        String logFilePath = FileUtil.getTmpDirPath() + getTmpTaskRelLogFilePath(taskId, instanceId);
        File logFile = new File(logFilePath);
        if (FileUtil.exist(logFile)) {
            String minioLogFileKey = getMinioTaskLogFileKey(taskId, instanceId);
            s3Service.putObject(minioLogFileKey, FileUtil.getInputStream(logFile));
            FileUtil.del(logFile);
        }
    }

    private String getMinioConsoleLogFileKey(String taskId, String instanceId) {
        return "tasks/" + taskId + "/" + instanceId + "/console.log";
    }

    private String getMinioTaskLogFileKey(String taskId, String instanceId) {
        return "tasks/" + taskId + "/" + instanceId + "/log.txt";
    }

    private String getTmpTaskRelLogFilePath(String taskId, String instanceId) {
        return "/data-scalpel-task-logs/" + taskId + "/" + instanceId + "/log.txt";
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Disable auto-commit of offsets
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // 每次poll最多拉取100条记录

        batchConsumer = new KafkaConsumer<>(properties);
        batchConsumer.subscribe(Collections.singletonList(KafkaTopic.TOPIC_TASK_LOG));
    }

    @EventListener
    public void handlerTaskInstanceCompletedEvent(TaskInstanceCompleteEvent taskInstanceCompleteEvent) {
        try {
            TaskResult taskResult = taskInstanceCompleteEvent.getTaskResult();
            archiveTaskLogToS3(taskResult.getTaskId(), taskResult.getTaskInstanceId());
            handleTaskInstanceCompleted(taskResult);
        } catch (Exception e) {
            log.error("归档日志到S3失败：" + e.getMessage(), e);
        }
    }

    public List<TaskLog> getTaskLog(String taskId, String taskInstanceId) throws Exception {
        List<TaskLog> logs = new ArrayList<>();

        String logFilePath = FileUtil.getTmpDirPath() + getTmpTaskRelLogFilePath(taskId, taskInstanceId);
        File logFile = new File(logFilePath);
        InputStream inputStream = null;
        if (FileUtil.exist(logFile)) {
            inputStream = FileUtil.getInputStream(logFile);
        } else {
            String minioLogFileKey = getMinioTaskLogFileKey(taskId, taskInstanceId);
            inputStream = s3Service.getObject(minioLogFileKey);
        }
        List<String> logTextList = IoUtil.readUtf8Lines(inputStream, new ArrayList<String>());
        inputStream.close();
        logTextList.forEach(content -> {
            try {
                logs.add(objectMapper.readValue(content, TaskLog.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return logs;
    }

    public InputStream getTaskConsoleLog(String taskId, String taskInstanceId) throws Exception {
        String minioLogFileKey = getMinioConsoleLogFileKey(taskId, taskInstanceId);
        return s3Service.getObject(minioLogFileKey);
    }
}
