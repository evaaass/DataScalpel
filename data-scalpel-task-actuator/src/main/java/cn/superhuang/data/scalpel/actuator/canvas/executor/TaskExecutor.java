package cn.superhuang.data.scalpel.actuator.canvas.executor;

import cn.superhuang.data.scalpel.actuator.ActuatorContext;
import cn.superhuang.data.scalpel.app.task.model.TaskResultSummary;

import java.io.Serial;
import java.io.Serializable;

public abstract class TaskExecutor implements Serializable {

    @Serial
    private static final long serialVersionUID = -6081105359314938119L;
    private ActuatorContext context;

    public TaskExecutor(ActuatorContext context) {
        this.context = context;
    }


    public abstract TaskResultSummary execute() throws Exception;

    public void destroy() throws Exception {
    }


    public ActuatorContext getContext() {
        return context;
    }

    public void setContext(ActuatorContext context) {
        this.context = context;
    }
}
