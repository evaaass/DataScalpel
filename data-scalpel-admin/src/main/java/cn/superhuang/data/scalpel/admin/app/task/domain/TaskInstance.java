package cn.superhuang.data.scalpel.admin.app.task.domain;

import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * A TaskInstance.
 */
@Entity
@Table(name = "admin_task_instance")
@Data
public class TaskInstance extends AbstractAuditingEntity<String>  implements Serializable {

    @Serial
    private static final long serialVersionUID = -8458131805209545032L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column
    private String taskId;

    @Column(unique = true)
    private String channelId;

    @Column
    private Date scheduledTriggerTime;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskInstanceExecutionStatus status;
}
