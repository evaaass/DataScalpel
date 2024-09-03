package cn.superhuang.data.scalpel.admin.app.model.domain;

import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.model.enumeration.ModelState;
import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.Instant;

/**
 * A LakeItem.
 */
@Data
@Entity
@Table(name = "admin_model")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Model extends AbstractAuditingEntity<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "catalog_id")
    private String catalogId;
    private String datasourceId;
    @Column(name = "name")
    private String name;
    @Column
    private String alias;
    @Column
    private String description;
    @Enumerated(EnumType.STRING)
    private ModelState state;

    private Long recordCount;
}
