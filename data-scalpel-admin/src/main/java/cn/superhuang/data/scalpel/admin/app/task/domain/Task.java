package cn.superhuang.data.scalpel.admin.app.task.domain;

import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskCategory;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskScheduleType;
import cn.superhuang.data.scalpel.admin.model.enumeration.TaskStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskType taskType;

    @Column(name = "catalog_id")
    private String catalogId;

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

    @Schema(description = "CRON表达式：scheduleType为CRON时必填")
    @Column(name = "cron")
    private String cron;

    @Schema(description = "调度起始时间：CRON时必填")
    @Column(name = "start_time")
    private Date startTime;

    @Schema(description = "调度结束时间：CRON时必填")
    @Column(name = "end_time")
    private Date endTime;
}
