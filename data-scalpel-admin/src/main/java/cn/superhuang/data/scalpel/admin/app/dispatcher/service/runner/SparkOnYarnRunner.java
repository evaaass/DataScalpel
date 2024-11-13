package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;

public class SparkOnYarnRunner implements SparkTaskRunner {
    @Override
    public String run(SparkTaskConfiguration taskConfiguration) {
        return "";
    }

    @Override
    public void kill(String channel) {

    }

    @Override
    public TaskRunnerInstanceInfo getInfo(String channel) {
        return null;
    }

    @Override
    public String getLog(String channelId) throws Exception {
        return "";
    }
}
