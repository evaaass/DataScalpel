package cn.superhuang.data.scalpel.admin.model.dto;

import cn.superhuang.data.scalpel.admin.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
public class TaskDTO {
    private String id;

    private String name;

    private TaskStatus status;

    private TaskType taskType;

    private String content;

    private Instant createTime;

    private Instant modifyTime;

    private String catalogId;

    private TaskInstanceExecutionStatus taskLastRunStatus;

    private Long successCount;

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
