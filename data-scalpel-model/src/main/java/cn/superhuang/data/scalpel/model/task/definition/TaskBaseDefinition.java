package cn.superhuang.data.scalpel.model.task.definition;

import cn.superhuang.data.scalpel.model.datasource.config.ApiConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BatchCanvasTaskDefinition.class, name = "BATCH_CANVAS"),
        @JsonSubTypes.Type(value = StreamCanvasTaskDefinition.class, name = "STREAM_CANVAS")
})
public class TaskBaseDefinition {
    private TaskType type;
}
