package cn.superhuang.data.scalpel.admin.app.task.domain;

import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.model.enumeration.ScriptType;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * A Script.
 */
@Data
@Entity
@Table(name = "admin_task_script")
public class TaskScript extends AbstractAuditingEntity<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 8871173663678316554L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ScriptType type;

    @Column(name = "catalog_id")
    private String catalogId;

    @Column(name = "script_definition")
    public String scriptDefinition;

}
