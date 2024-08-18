package cn.superhuang.data.scalpel.admin.app.service.impl.boot;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TaskJarRunnable implements Callable<String> {

    private TaskInstance taskInstance;
    private List<String> localRunCommand;

    public TaskJarRunnable(TaskInstance taskInstance, List<String> localRunCommand) {
        this.taskInstance = taskInstance;
        this.localRunCommand = localRunCommand;
    }

    @Override
    public String call() throws Exception {
        List<String> cmdList = new ArrayList<>();
        cmdList.addAll(localRunCommand);
        cmdList.add(taskInstance.getTaskId());
        cmdList.add(taskInstance.getId());
        System.out.println(CollectionUtil.join(cmdList, " "));
        String execResult = RuntimeUtil.execForStr(cmdList.toArray(String[]::new));
        System.out.println(execResult);
        return execResult;
    }
}
