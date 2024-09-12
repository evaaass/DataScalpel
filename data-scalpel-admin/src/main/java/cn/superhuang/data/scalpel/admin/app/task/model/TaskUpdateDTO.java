package cn.superhuang.data.scalpel.admin.app.task.model;

import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.TaskStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class TaskUpdateDTO {
    private String id;

    private String name;

    private TaskStatus status;

    private String definition;

    private String catalogId;

    @Schema(description = "调度类型：TIMER为运行一次，CRON为周期运行,NONE为暂不调度")
    private TaskScheduleType scheduleType;
    @Schema(description = "周期类型：scheduleType为CRON时必填")
    private TaskCycleType cycleType;
    @Schema(description = "CRON表达式：scheduleType为CRON时必填")
    private String cron;
    @Schema(description = "调度起始时间：CRON时必填")
    private Date startTime;
    @Schema(description = "调度结束时间：CRON时必填")
    private Date endTime;
}
