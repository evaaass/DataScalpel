package cn.superhuang.data.scalpel.admin.app.task.service.executor;


import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class TaskBaseExecutor {

    public abstract Boolean support(TaskType type);

    public abstract void execute(Task task, TaskInstance taskInstance) throws Exception;
}
