package cn.superhuang.data.scalpel.model.task;

import cn.superhuang.data.scalpel.app.task.model.TaskResultSummary;
import lombok.Data;

import java.util.Date;

@Data
public class TaskResult {
    private Boolean success;
    private String message;
    private String detail;

    private Date startTime;
    private Date endTime;

    private String taskId;
    private String taskInstanceId;

    private TaskResultSummary summary;


    public static TaskResult errorResult(String message, String detail) {
        TaskResult taskResult = new TaskResult();
        taskResult.setSuccess(false);
        taskResult.setMessage(message);
        taskResult.setDetail(detail);
        return taskResult;
    }

    public String getUniqueKey() {
        return taskId + "_" + taskInstanceId;
    }
}
