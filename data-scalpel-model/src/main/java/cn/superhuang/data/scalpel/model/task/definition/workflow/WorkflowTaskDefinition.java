package cn.superhuang.data.scalpel.model.task.definition.workflow;

import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowTaskDefinition extends TaskBaseDefinition {
    private List<WorkflowNode> nodes;
    private List<WorkflowLine> lines;
}
