package cn.superhuang.data.scalpel.model.task.configuration;

import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

import java.util.List;
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

    private Map<String, String> params;

    //    //TODO
//    private List inputItems;
//    //TODO
//    private List outputSinks;
    //TODO
    private Map<String, ModelDTO> modelMap;
    //TODO
    private Map<String, DatasourceConfig> datasourceMap;
}
