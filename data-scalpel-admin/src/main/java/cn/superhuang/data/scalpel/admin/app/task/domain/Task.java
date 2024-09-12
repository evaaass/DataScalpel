package cn.superhuang.data.scalpel.admin.app.task.domain;

import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.config.RequestParamsErrorException;
import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.TaskStatus;
import cn.superhuang.data.scalpel.admin.util.CronUtil;
import cn.superhuang.data.scalpel.model.enumeration.TaskCycleType;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * A Task.
 */
@Data
@Entity
@Table(name = "admin_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Task extends AbstractAuditingEntity<String> implements Serializable {


    @Serial
    private static final long serialVersionUID = -3724081601026690153L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "catalog_id")
    private String catalogId;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskInstanceExecutionStatus taskLastRunStatus;

    @Column(name = "success_count")
    private Long successCount;

    @Column(name = "failure_count")
    private Long failureCount;

    @Column(name = "任务定义")
    private String definition;

    @Schema(description = "调度类型：TIMER为运行一次，CRON为周期运行,NONE为暂不调度")
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type")
    private TaskScheduleType scheduleType;

    @Schema(description = "间隔时间（分钟）：scheduleType为INTERVAL时必填")
    private Integer interval;

    @Schema(description = "周期类型：scheduleType为CRON时必填")
    @Enumerated(EnumType.STRING)
    private TaskCycleType cycleType;

    @Schema(description = "CRON表达式：scheduleType为CRON时必填")
    @Column(name = "cron")
    private String cron;

    @Schema(description = "调度起始时间：CRON时必填")
    @Column(name = "start_time")
    private Date startTime;

    @Schema(description = "调度结束时间：CRON时必填")
    @Column(name = "end_time")
    private Date endTime;


    public void validate() {
        if (scheduleType == null) {
            throw new RequestParamsErrorException("调度类型不能为空");
        }
        if (startTime == null && (scheduleType.equals(TaskScheduleType.CRON) || scheduleType.equals(TaskScheduleType.TIMER) || scheduleType.equals(TaskScheduleType.INTERVAL))) {
            throw new RequestParamsErrorException("调度类型为%s时，开始时间不能为空".formatted(scheduleType));
        }
        if (endTime == null && (scheduleType.equals(TaskScheduleType.CRON) || scheduleType.equals(TaskScheduleType.INTERVAL))) {
            throw new RequestParamsErrorException("调度类型为%s时，结束时间不能为空".formatted(scheduleType));
        }
        if (interval == null && scheduleType == TaskScheduleType.INTERVAL) {
            throw new RequestParamsErrorException("调度类型为%s时，间隔时间不能为空".formatted(scheduleType));
        }
        if (scheduleType.equals(TaskScheduleType.CRON)) {
            if (StrUtil.isEmpty(cron)) {
                throw new RequestParamsErrorException("cron表达式不能为空");
            }
            if (cycleType == null) {
                throw new RequestParamsErrorException("周期类型不能为空");
            }
            CronUtil.validateCycleCron(cycleType, cron);
        }
    }
}
