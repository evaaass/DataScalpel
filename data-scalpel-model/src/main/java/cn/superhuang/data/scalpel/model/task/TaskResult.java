package cn.superhuang.data.scalpel.model.task;

import lombok.Data;

import java.util.Date;

@Data
public class TaskResult {
    private Boolean success;
    private String message;
    private String detail;

    private Date startTime;
    private Date endTime;
}
