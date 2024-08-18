package cn.superhuang.data.scalpel.model.datasource.config;

import lombok.Data;

@Data
public class KafkaConfig extends DatasourceConfig{
    public static final String KAFKA_BOOTSTRAP_SERVERS="bootstrap_servers";


    public String getBootstrapServers() {
        return getParams().get(KAFKA_BOOTSTRAP_SERVERS);
    }

    public void setBootstrapServers(String bootstrapServers) {
        getParams().put(KAFKA_BOOTSTRAP_SERVERS, bootstrapServers);
    }
}