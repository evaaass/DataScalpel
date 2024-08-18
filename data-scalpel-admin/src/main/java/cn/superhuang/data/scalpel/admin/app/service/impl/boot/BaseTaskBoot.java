package cn.superhuang.data.scalpel.admin.app.service.impl.boot;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.model.TaskSubmitResult;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskBootType;

public interface BaseTaskBoot {

    public Boolean canHandle(TaskBootType type);

    public TaskSubmitResult submitTask(TaskInstance instance);
}
