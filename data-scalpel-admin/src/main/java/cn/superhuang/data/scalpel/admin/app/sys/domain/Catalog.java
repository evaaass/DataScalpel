package cn.superhuang.data.scalpel.admin.app.sys.domain;

import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

/**
 * A Catalog.
 */
@Data
@Entity
@Table(name = "admin_catalog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Catalog extends AbstractAuditingEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    private String fullIdPath;

    private String fullNamePath;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "index")
    private Integer index;

    @Column(name = "parent_id")
    private String parentId;
}
