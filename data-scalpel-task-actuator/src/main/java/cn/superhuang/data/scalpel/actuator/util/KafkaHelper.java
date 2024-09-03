package cn.superhuang.data.scalpel.actuator.util;

import cn.superhuang.data.scalpel.app.constant.KafkaTopic;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
public class KafkaHelper {

    private final KafkaProducer<String, String> producer;

    public KafkaHelper() {
        producer = null;
    }

    public KafkaHelper(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    public Future<RecordMetadata> sendMessage(String topic, String key, String value) {
        if (producer == null) {
            System.out.println(topic + "|" + value);
        }
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        return producer.send(record);
    }

//    public void sendMessage(String topic, String key, String value, Callback callback) {
//        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
//        producer.send(record, callback);
//    }

    public void sendLog(TaskLog taskLog) {
        try {
            sendMessage(KafkaTopic.TOPIC_TASK_LOG, null, JsonUtil.objectMapper.writeValueAsString(taskLog));
        } catch (Exception e) {
            log.error("发送日志失败:" + e.getMessage());
        }
    }

    public void sendResult(TaskResult result) {
        try {
            sendMessage(KafkaTopic.TOPIC_DISPATCHER_TASK_RESULT, null, JsonUtil.objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("发送任务结果失败:" + e.getMessage());
        }
    }
}
