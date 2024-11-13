package cn.superhuang.data.scalpel.model.task.configuration;

import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowTaskDefinition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.Map;


@Data
public class WorkflowTaskConfiguration extends TaskConfiguration {
    private WorkflowTaskDefinition taskDefinition;

    @Override
    public TaskBaseDefinition getTaskDefinition() {
        return taskDefinition;
    }
}
