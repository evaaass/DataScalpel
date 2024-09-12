package cn.superhuang.data.scalpel.admin.event;



import cn.superhuang.data.scalpel.model.task.TaskRunningStatus;
import org.springframework.context.ApplicationEvent;


public class TaskRunningStateChangeEvent extends ApplicationEvent {
    private TaskRunningStatus status;

    public TaskRunningStateChangeEvent(Object source, TaskRunningStatus status) {
        super(source);
        this.status = status;
    }

    public TaskRunningStatus getStatus() {
        return status;
    }

    public void setStatus(TaskRunningStatus status) {
        this.status = status;
    }
}
