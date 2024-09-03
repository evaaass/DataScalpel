package cn.superhuang.data.scalpel.admin.app.item.domain;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * A LakeItem.
 */
@Data
@Entity
@Table(name = "admin_lake_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LakeItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 794703006152754350L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @Column(name = "id")
    private String id;

    @Column(name = "catalog_id")
    private String catalogId;

    @Column(name = "catalog_uri")
    private String catalogUri;

    private String datasourceId;

    @Column(name = "name")
    private String name;

    @Column
    private String cnName;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;

    @Column(name = "metadata_type")
    private String metadataType;
    @Lob
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "modify_time")
    private Instant modifyTime;

}
