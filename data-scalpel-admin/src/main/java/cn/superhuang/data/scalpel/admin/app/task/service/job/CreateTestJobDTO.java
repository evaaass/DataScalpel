package cn.superhuang.data.scalpel.admin.app.task.service.job;

import lombok.Data;

@Data
public class CreateTestJobDTO {
    private String jobName;
    private String jobGroup;
    private String jobDesc;

    private String triggerName;
    private String triggerGroup;
    private String triggerDesc;
    private String cron;

    private String value;
}
