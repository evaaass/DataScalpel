package cn.superhuang.data.scalpel.admin.app.sys.domain;

import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.app.sys.model.emun.LogTargetType;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "admin_sys_log")
public class SysLog extends AbstractAuditingEntity<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2430667329515281391L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Enumerated(EnumType.STRING)
    private LogTargetType logTargetType;
    private String logTargetId;
    @Enumerated(EnumType.STRING)
    private LogLevel level;
    private String message;
    private String detail;
    private LocalDateTime logTime;
    private LocalDateTime createTime;
}
