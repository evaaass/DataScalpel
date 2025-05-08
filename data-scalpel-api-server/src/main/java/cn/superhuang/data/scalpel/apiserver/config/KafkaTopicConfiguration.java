package cn.superhuang.data.scalpel.apiserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(
        prefix = "data-bank.kafka-topics"
)
@Configuration
@Data
public class KafkaTopicConfiguration {
    private String runningState;
}
