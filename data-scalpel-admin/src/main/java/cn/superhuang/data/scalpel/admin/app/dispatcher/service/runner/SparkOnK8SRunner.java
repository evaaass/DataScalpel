package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;

public class SparkOnK8SRunner implements ITaskRunner{
    @Override
    public String run(TaskConfiguration taskConfiguration) {
        return "";
    }

    @Override
    public void kill(String channel) {

    }

    @Override
    public TaskRunnerInstanceInfo getInfo(String channel) {
        return null;
    }
}
