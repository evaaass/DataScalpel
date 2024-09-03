package cn.superhuang.data.scalpel.model.datasource.config;

import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"bootstrapServers","options"})
public class KafkaConfig extends DatasourceConfig{
    public static final String KAFKA_BOOTSTRAP_SERVERS="bootstrap_servers";

    public KafkaConfig(){
        this.setType(DatasourceType.KAFKA);
    }

    public String getBootstrapServers() {
        return getParams().get(KAFKA_BOOTSTRAP_SERVERS);
    }

    public void setBootstrapServers(String bootstrapServers) {
        getParams().put(KAFKA_BOOTSTRAP_SERVERS, bootstrapServers);
    }
}