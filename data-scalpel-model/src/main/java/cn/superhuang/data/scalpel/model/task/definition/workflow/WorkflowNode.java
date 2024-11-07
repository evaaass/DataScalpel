package cn.superhuang.data.scalpel.model.task.definition.workflow;

import lombok.Data;

import java.util.Map;

@Data
public class WorkflowNode {
    private String id;
    private String name;
    private WorkflowNodeType type;
    private String description;
    private Map<String, String> options;


    private Boolean executed = false;
}