package cn.superhuang.data.scalpel.admin.app.dispatcher.model;

import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import lombok.Data;

import java.util.Date;

@Data
public class RunningTaskInfo {
    private String channelId;
    private TaskConfiguration taskConfiguration;
    private Date startDate;

    public RunningTaskInfo(String channelId, TaskConfiguration taskConfiguration) {
        this.channelId = channelId;
        this.taskConfiguration = taskConfiguration;
        this.startDate = new Date();
    }

    public Boolean isTimeout(Integer timeout) {
        return new Date().getTime() - startDate.getTime() > timeout;
    }
}