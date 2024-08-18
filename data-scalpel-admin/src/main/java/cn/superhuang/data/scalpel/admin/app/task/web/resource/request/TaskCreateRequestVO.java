package cn.superhuang.data.scalpel.admin.app.task.web.resource.request;

import cn.superhuang.data.scalpel.admin.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class TaskCreateRequestVO {
    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务类型：画布模式传CANVAS就行")
    private TaskType taskType;

    @Schema(description = "任务内容：CANVAS拼成的JSON放着里面九号")
    private String content;

    @Schema(description = "目录ID")
    private String catalogId;

    @Schema(description = "调度类型：TIMER为运行一次，CRON为周期运行,NONE为暂不调度")
    private TaskScheduleType scheduleType;
    @Schema(description = "CRON表达式：scheduleType为CRON时必填")
    private String cron;
    @Schema(description = "调度起始时间：CRON时必填")
    private Date startTime;
    @Schema(description = "调度结束时间：CRON时必填")
    private Date endTime;
}
