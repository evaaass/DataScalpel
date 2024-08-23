package cn.superhuang.data.scalpel.model.task.configuration;

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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CanvasTaskConfiguration.class, name = "CANVAS"),
})
@Data
public abstract class TaskConfiguration {
    private String taskId;
    private String taskName;
    private String taskInstanceId;

    private SparkConfiguration sparkConfiguration;

    private TaskType type;
    /**
     * kafka连接信息
     */
    private KafkaConfig kafkaConfig;
    /**
     * 周期类型
     */
    private TaskCycleType cycleType;
    /**
     * 计划执行时间
     */
    private Long planTriggerTime;

    private Map<String, String> params;

    //    //TODO
//    private List inputItems;
//    //TODO
//    private List outputSinks;
    //TODO
    private Map<String, ModelDTO> modelMap;
    //TODO
    private Map<String, DatasourceConfig> datasourceMap;

    @JsonIgnore
    public String getUniqueKey() {
        return taskId + "_" + taskInstanceId;
    }
}
