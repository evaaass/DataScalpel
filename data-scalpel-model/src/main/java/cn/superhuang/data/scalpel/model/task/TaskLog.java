package cn.superhuang.data.scalpel.model.task;

import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import lombok.Data;

import java.util.Date;

@Data
public class TaskLog {
    private String taskInstanceId;
    private LogLevel level;
    private Date createTime;
    private String message;
    private String detail;
}
