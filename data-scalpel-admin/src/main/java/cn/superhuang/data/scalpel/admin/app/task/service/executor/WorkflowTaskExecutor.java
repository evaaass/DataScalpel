package cn.superhuang.data.scalpel.admin.app.task.service.executor;


import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;
import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceTriggerCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.workflow.WorkflowTaskNodeExecutor;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowLine;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowNode;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowTaskDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class WorkflowTaskExecutor extends TaskBaseExecutor {

    @Resource
    private WorkflowTaskNodeExecutor executor;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 1000, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Override
    public Boolean support(TaskType type) {
        return type == TaskType.WORKFLOW;
    }

    @Override
    public void execute(Task task, TaskInstance taskInstance) throws Exception {
        String channelId = null;
        applicationEventPublisher.publishEvent(new TaskInstanceTriggerCompleteEvent(this, TaskTriggerResult.builder().taskId(task.getId()).taskInstanceId(taskInstance.getId()).success(true).channelId(channelId).build()));
        TaskBaseDefinition taskBaseDefinition = objectMapper.readValue(task.getDefinition(), TaskBaseDefinition.class);
        WorkflowTaskDefinition definition = (WorkflowTaskDefinition) taskBaseDefinition;
        Future future = threadPoolExecutor.submit(() -> {
        });
        //从task中获取任务超时时间
        Object taskRes = future.get(120, TimeUnit.MINUTES);
        Set<WorkflowNode> startNodes = getStartNodes(definition);
        Map<String, WorkflowNode> nodeMap = definition.getNodes().stream().collect(Collectors.toMap(WorkflowNode::getId, t -> t));
        recursionRun(startNodes, nodeMap, definition.getLines());
    }

    public void recursionRun(Collection<WorkflowNode> nodes, Map<String, WorkflowNode> nodeMap, List<WorkflowLine> lines) {
        for (WorkflowNode node : nodes) {
            if (node.getExecuted()) {
                continue;
            }
            executor.execute(node);
            node.setExecuted(true);
            Collection<WorkflowNode> nextNodes = lines.stream().filter(line -> line.getFrom().equals(node.getId())).map(line -> nodeMap.get(line.getTo())).collect(Collectors.toSet());
            recursionRun(nextNodes, nodeMap, lines);
        }
    }


    public Set<WorkflowNode> getStartNodes(WorkflowTaskDefinition definition) {
        return definition.getNodes().stream().filter(node -> {
            long nodeInputLine = definition.getLines().stream().filter(line -> line.getTo().equals(node.getId())).count();
            return nodeInputLine == 0;
        }).collect(Collectors.toSet());
    }


}
