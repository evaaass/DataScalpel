package cn.superhuang.data.scalpel.admin.app.task.web.resource.request;

import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class TaskUpdateRequestVO {
    private String name;

    private String catalogId;


    @Schema(description = "调度类型：TIMER为运行一次，CRON为周期运行,NONE为暂不调度")
    private TaskScheduleType scheduleType;
    @Schema(description = "间隔时间（分钟）：scheduleType为INTERVAL时必填")
    private Integer interval;
    @Schema(description = "周期类型：scheduleType为CRON时必填")
    private TaskCycleType cycleType;
    @Schema(description = "CRON表达式：scheduleType为CRON时必填")
    private String cron;
    @Schema(description = "调度起始时间：CRON,TIMER,INTERVAL时必填")
    private Date startTime;
    @Schema(description = "调度结束时间：CRON,INTERVAL时必填时必填")
    private Date endTime;
}