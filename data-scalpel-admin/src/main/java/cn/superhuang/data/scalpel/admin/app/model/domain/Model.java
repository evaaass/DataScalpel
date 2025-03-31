package cn.superhuang.data.scalpel.admin.app.model.domain;

import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.app.model.model.enumeration.ModelState;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

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

    private String physicalTable;
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
