package cn.superhuang.data.scalpel.model.task;

import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import lombok.Data;

@Data
public class TaskRunningStatus {
    private String channelId;
    private TaskInstanceExecutionStatus state;
    private String message;
    private String detail;
}
