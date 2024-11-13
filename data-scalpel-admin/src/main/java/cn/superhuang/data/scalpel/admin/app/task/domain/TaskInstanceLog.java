package cn.superhuang.data.scalpel.admin.app.task.domain;

import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * A TaskInstance.
 */
@Entity
@Table(name = "admin_task_instance_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskInstanceLog  implements Serializable {

    @Serial
    private static final long serialVersionUID = -7478066033057310876L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column
    private String taskId;

    @Column
    private String taskInstanceId;

    @Enumerated(EnumType.STRING)
    @Column
    private LogLevel level;

    @Column(length = 2000)
    private String message;

    @Column(columnDefinition = "TEXT")
    private String detail;

    private Date createTime;

}
