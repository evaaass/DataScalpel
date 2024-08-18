package cn.superhuang.data.scalpel.admin.app.item.domain;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.MetadataTypeCategory;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

@Data
@Entity
@Table(name = "admin_metadata_type")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MetadataType {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_category")
    private MetadataTypeCategory typeCategory;

    private String superType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "admin_metadata_type_attributes")
    private Set<MetadataAttribute> attributes;
}
