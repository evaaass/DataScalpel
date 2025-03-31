package cn.superhuang.data.scalpel.admin.app.task.service.executor;


import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;
import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceTriggerCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.workflow.WorkflowTaskNodeExecutor;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowLine;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowNode;
import cn.superhuang.data.scalpel.model.task.definition.workflow.WorkflowTaskDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;
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
    private final ConcurrentHashMap<String, Future<?>> runningTaskMap = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 1000, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private final ScheduledExecutorService taskTimeoutMonitorExecutor = Executors.newScheduledThreadPool(1);


    @Override
    public Boolean support(TaskType type) {
        return type == TaskType.WORKFLOW;
    }

    @Override
    public void execute(Task task, TaskInstance taskInstance) throws Exception {

        String channelId = StrUtil.uuid();
        TaskBaseDefinition taskBaseDefinition = objectMapper.readValue(task.getDefinition(), TaskBaseDefinition.class);
        WorkflowTaskDefinition definition = (WorkflowTaskDefinition) taskBaseDefinition;
        Future<?> future = threadPoolExecutor.submit(() -> {
            Date startTime = new Date();
            Set<WorkflowNode> startNodes = getStartNodes(definition);
            Map<String, WorkflowNode> nodeMap = definition.getNodes().stream().collect(Collectors.toMap(WorkflowNode::getId, t -> t));
            recursionRun(startNodes, nodeMap, definition.getLines());
            applicationEventPublisher.publishEvent(new TaskInstanceCompleteEvent(this, TaskResult.builder().taskId(task.getId()).taskInstanceId(taskInstance.getId()).success(true).startTime(startTime).endTime(new Date()).build(), null));
        });
        runningTaskMap.put(channelId, future);
        applicationEventPublisher.publishEvent(new TaskInstanceTriggerCompleteEvent(this, TaskTriggerResult.builder().taskId(task.getId()).taskInstanceId(taskInstance.getId()).success(true).channelId(channelId).build()));

        int taskTimeout = Integer.parseInt(task.getOptions().get("SYS_TIMEOUT"));
        taskTimeoutMonitorExecutor.schedule(() -> {
            if (future.isDone()) {
                return;
            }
            future.cancel(true);
            //TODO 是不是应该加个逻辑，强行结束正在运行的任务
            applicationEventPublisher.publishEvent(new TaskInstanceCompleteEvent(this, TaskResult.builder().taskId(task.getId()).taskInstanceId(taskInstance.getId()).success(false).message("任务运行时间超过%s，强制中断".formatted(taskTimeout)).build(), null));
        }, taskTimeout, TimeUnit.MINUTES);
    }

    @Override
    public void kill(String channelId) {
        if (!runningTaskMap.containsKey(channelId)) {
            return;
        }
        runningTaskMap.get(channelId).cancel(true);
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
