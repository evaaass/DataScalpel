package cn.superhuang.data.scalpel.model.task.configuration;

import cn.superhuang.data.scalpel.app.constant.TaskOptions;
import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.Map;


@Data
public abstract class SparkTaskConfiguration extends TaskConfiguration {
    private SparkConfiguration sparkConfiguration;
    private KafkaConfig kafkaConfig;
    private Map<String, ModelDTO> modelMap;
    private Map<String, DatasourceConfig> datasourceMap;

    @JsonIgnore
    public Integer getCpu() {
        return getOption(TaskOptions.TASK_CPU) == null ? null : Integer.parseInt(getOption(TaskOptions.TASK_CPU));
    }

    @JsonIgnore
    public Integer getMemory() {
        return getOption(TaskOptions.TASK_MEMORY) == null ? null : Integer.parseInt(getOption(TaskOptions.TASK_MEMORY));
    }
}
