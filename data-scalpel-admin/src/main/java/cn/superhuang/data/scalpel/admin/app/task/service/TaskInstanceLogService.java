package cn.superhuang.data.scalpel.admin.app.task.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstanceLog;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceLogRepository;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceCompleteEvent;
import cn.superhuang.data.scalpel.app.constant.KafkaTopic;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskInstanceLogService implements InitializingBean {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private KafkaConsumer<String, String> batchConsumer;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private TaskInstanceLogRepository taskInstanceLogRepository;


    public List<TaskLog> getTaskLog(String taskId, String taskInstanceId) throws Exception {
        List<TaskInstanceLog> taskInstanceLogs = taskInstanceLogRepository.findAllByTaskIdAndTaskInstanceIdOrderByCreateTimeDesc(taskId, taskInstanceId);
        return BeanUtil.copyToList(taskInstanceLogs, TaskLog.class);
    }

    //TODO 控制台日志抓了放到S3上去
    public InputStream getTaskConsoleLog(String taskId, String taskInstanceId) throws Exception {
        return IoUtil.toUtf8Stream("SuperHuang");
    }

    @Scheduled(fixedDelay = 5 * 1000)
    private void consumeTaskLog() {
        try {
            ConsumerRecords<String, String> records = batchConsumer.poll(Duration.ofMillis(5 * 1000)); // poll间隔1秒
            List<TaskInstanceLog> logs = new ArrayList<>(records.count());
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                String logContent = record.value();
                try {
                    TaskLog log = objectMapper.readValue(logContent, TaskLog.class);
                    TaskInstanceLog taskInstanceLog = new TaskInstanceLog();
                    taskInstanceLog.setTaskId(log.getTaskId());
                    taskInstanceLog.setTaskInstanceId(log.getTaskInstanceId());
                    taskInstanceLog.setLevel(log.getLevel());
                    taskInstanceLog.setMessage(log.getMessage());
                    taskInstanceLog.setDetail(log.getDetail());
                    logs.add(taskInstanceLog);
                } catch (Exception e) {
                    log.error("解析日志失败：" + logContent, e.getMessage());
                }
            }
            // 手动提交偏移量
            batchConsumer.commitSync();
            if (logs.isEmpty()) {
                return;
            }
            taskInstanceLogRepository.saveAll(logs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析日志失败,error:" + e.getMessage());
        }
    }

    /**
     * 任务最终的成功和失败日志由这个方法处理
     *
     * @param taskInstanceCompleteEvent
     */
    @EventListener
    public void handlerTaskInstanceCompletedEvent(TaskInstanceCompleteEvent taskInstanceCompleteEvent) {
        TaskResult taskResult = taskInstanceCompleteEvent.getTaskResult();
        TaskInstanceLog log = new TaskInstanceLog();
        log.setTaskId(taskResult.getTaskId());
        log.setTaskInstanceId(taskResult.getTaskInstanceId());
        log.setCreateTime(new Date());
        if (taskResult.getSuccess()) {
            log.setLevel(LogLevel.INFO);
            log.setMessage("任务执行成功");
        } else {
            log.setLevel(LogLevel.ERROR);
            log.setMessage(taskResult.getMessage());
            log.setDetail(taskResult.getDetail());
        }
        taskInstanceLogRepository.save(log);

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
}