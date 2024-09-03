package cn.superhuang.data.scalpel.admin.app.task.service.event;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskInstanceTriggerCompleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1198041213423224055L;
    final private TaskTriggerResult taskTriggerResult ;

    public TaskInstanceTriggerCompleteEvent(Object source, TaskTriggerResult taskTriggerResult) {
        super(source);
        this.taskTriggerResult = taskTriggerResult;
    }

}
