package cn.superhuang.data.scalpel.model.task.configuration;

import cn.superhuang.data.scalpel.app.constant.TaskOptions;
import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CanvasTaskConfiguration.class, name = "BATCH_CANVAS"),
        @JsonSubTypes.Type(value = WorkflowTaskConfiguration.class, name = "WORKFLOW"),

})
@Data
public abstract class TaskConfiguration {
    private Boolean debug;
    private String taskId;
    private String taskName;
    private String taskInstanceId;
    private Map<String, String> options;
    private TaskCycleType cycleType;
    private Long planTriggerTime;

    public abstract TaskBaseDefinition getTaskDefinition();

    @JsonIgnore
    public String getUniqueKey() {
        return taskId + "_" + taskInstanceId;
    }

    public String getOption(String key) {
        return options.get(key);
    }

    @JsonIgnore
    public Integer getTaskTimeout() {
        return getOption(TaskOptions.TASK_TIMEOUT) == null ? null : Integer.parseInt(getOption(TaskOptions.TASK_TIMEOUT));
    }

}
