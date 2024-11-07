package cn.superhuang.data.scalpel.model.task.definition.workflow;

import lombok.Data;

import java.util.Map;

@Data
public class WorkflowNodeResult {
    private Boolean success;
    private String message;
    private String detail;

    private Map<String, String> props;

}