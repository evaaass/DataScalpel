package cn.superhuang.data.scalpel.admin.app.task.service.event;

import cn.superhuang.data.scalpel.model.task.TaskResult;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskInstanceCompleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7978975285020348980L;
    final private TaskResult taskResult;

    public TaskInstanceCompleteEvent(Object source, TaskResult taskResult) {
        super(source);
        this.taskResult = taskResult;
    }

}
