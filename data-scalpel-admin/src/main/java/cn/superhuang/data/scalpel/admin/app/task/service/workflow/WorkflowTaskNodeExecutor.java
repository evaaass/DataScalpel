package cn.superhuang.data.scalpel.admin.app.task.service.workflow;

import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowNode;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowNodeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkflowTaskNodeExecutor extends WorkflowNodeExecutor {
    @Override
    public WorkflowNodeResult execute(WorkflowNode workflowNode) {
        log.info("工作流执行任务：" + workflowNode.getName());
        WorkflowNodeResult result = new WorkflowNodeResult();
        result.setSuccess(true);
        return result;
    }
}