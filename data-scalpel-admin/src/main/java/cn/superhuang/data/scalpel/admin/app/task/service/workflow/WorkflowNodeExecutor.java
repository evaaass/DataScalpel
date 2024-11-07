package cn.superhuang.data.scalpel.admin.app.task.service.workflow;

import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowNode;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowNodeResult;

public abstract class WorkflowNodeExecutor {

    public abstract WorkflowNodeResult execute(WorkflowNode workflowNode);
}
