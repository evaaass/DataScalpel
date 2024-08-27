package cn.superhuang.data.scalpel.admin.app.datafile.domain;

import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileState;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.repository.converter.MapConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.Map;

/**
 * A LakeItem.
 */
@Data
@Entity
@Table(name = "admin_data_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataFile extends AbstractAuditingEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "catalog_id")
    private String catalogId;

    @Column(name = "name")
    private String name;
    @Column
    private String alias;
    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private DataFileType type;

    @Enumerated(EnumType.STRING)
    @Column
    private DataFileState state;

    @Convert(converter = MapConverter.class)
    @Column(length = 1000)
    private Map<String, String> props;

    @Column(columnDefinition = "TEXT")
    private String metadata;
}
