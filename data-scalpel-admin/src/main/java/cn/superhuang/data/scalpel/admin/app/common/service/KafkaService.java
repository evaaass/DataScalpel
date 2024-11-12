package cn.superhuang.data.scalpel.admin.app.common.service;

import cn.superhuang.data.scalpel.app.constant.KafkaTopic;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;
import cn.superhuang.data.scalpel.model.task.TaskKill;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaService {


    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private ObjectMapper objectMapper;


    public Boolean send(String topic, Object msg) {
        try {
            CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send(new ProducerRecord<>(topic, objectMapper.writeValueAsString(msg)));
            SendResult<String, String> sendResult = send.get();
            System.out.println(sendResult);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean sendTaskKill(TaskKill taskKill) {
        return send(KafkaTopic.TOPIC_TASK_TRIGGER, taskKill);
    }

    public Boolean sendTaskTrigger(TaskConfiguration taskConfiguration) {
        return send(KafkaTopic.TOPIC_TASK_TRIGGER, taskConfiguration);
    }

    public Boolean sendTriggerResult(TaskTriggerResult taskTriggerResult) {
        return send(KafkaTopic.TOPIC_TASK_TRIGGER_RESULT, taskTriggerResult);
    }

    public Boolean sendTaskResult(TaskResult taskResult) {
        return send(KafkaTopic.TOPIC_TASK_RESULT, taskResult);
    }

}
