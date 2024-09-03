package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;

public interface ITaskRunner {

    //执行一次任务，返回ChannelId

    /**
     *
     * @param taskConfiguration 任务信息
     * @return 返回改次任务执行的唯一标识，唯一标识用channelId表示，通过该id可以获取到任务状态信息
     */
    String run(TaskConfiguration taskConfiguration) throws Exception;

    void kill(String channelId) throws Exception;

    TaskRunnerInstanceInfo getInfo(String channelId) throws Exception;

    String getLog(String channelId) throws Exception;
}