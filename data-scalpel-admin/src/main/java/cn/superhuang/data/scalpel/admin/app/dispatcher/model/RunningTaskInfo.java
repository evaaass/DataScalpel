package cn.superhuang.data.scalpel.admin.app.dispatcher.model;

import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import lombok.Data;

import java.util.Date;

@Data
public class RunningTaskInfo {
    private String channelId;
    private SparkTaskConfiguration taskConfiguration;
    private Date startDate;

    public RunningTaskInfo(String channelId, SparkTaskConfiguration taskConfiguration) {
        this.channelId = channelId;
        this.taskConfiguration = taskConfiguration;
        this.startDate = new Date();
    }

    public Boolean isTimeout(Integer defaultTimeout) {

        Integer timeout = defaultTimeout;
        if (taskConfiguration.getTaskTimeout() != null) {
            timeout = taskConfiguration.getTaskTimeout();
        }
        return new Date().getTime() - startDate.getTime() > timeout * 60 * 1000;
    }
}