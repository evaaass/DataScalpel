package cn.superhuang.data.scalpel.model.task;

import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import lombok.Data;

import java.util.Date;

@Data
public class TaskLog {
    private String taskId;
    private String taskInstanceId;
    private Long time;
    private LogLevel level;
    private String message;
    private String detail;
}
