package cn.superhuang.data.scalpel.admin.app.task.service.event;

import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskInstanceCompleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7978975285020348980L;
    final private TaskResult taskResult;
    final private SparkTaskConfiguration sparkTaskConfiguration;

    public TaskInstanceCompleteEvent(Object source, TaskResult taskResult, SparkTaskConfiguration sparkTaskConfiguration) {
        super(source);
        this.taskResult = taskResult;
        this.sparkTaskConfiguration = sparkTaskConfiguration;
    }

}
