package cn.superhuang.data.scalpel.admin.model.web.vo;

import cn.superhuang.data.scalpel.admin.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
public class TaskDetailVO {

    private String id;

    private String name;

    private TaskStatus status;
    @Schema(description = "任务类型，目前只有CANVAS")
    private TaskType taskType;
    @Schema(description = "任务配置信息")
    private String content;

    private Instant createTime;

    private Instant modifyTime;

    private String catalogId;

    @Schema(description = "任务最后一次运行状态；null则代表没运行过")
    private TaskInstanceExecutionStatus taskLastRunStatus;
    @Schema(description = "任务运行成功数")
    private Long successCount;
    @Schema(description = "任务运行失败数")
    private Long failureCount;

    @Schema(description = "调度类型：TIMER为运行一次，CRON为周期运行,NONE为暂不调度")
    private TaskScheduleType scheduleType;
    @Schema(description = "CRON表达式：scheduleType为CRON时必填")
    private String cron;
    @Schema(description = "调度起始时间：CRON时必填")
    private Date startTime;
    @Schema(description = "调度结束时间：CRON时必填")
    private Date endTime;

}
