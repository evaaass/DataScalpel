package cn.superhuang.data.scalpel.apiserver.domain;


import cn.superhuang.data.scalpel.converter.MapConverter;
import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * A Datasource.
 */
@Data
@Entity
@Table(name = "admin_datasource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Datasource extends AbstractAuditingEntity<String> implements Serializable {


    @Serial
    private static final long serialVersionUID = 2877831147094851129L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    
    @Column(name = "name", unique = true)
    private String name;
    
    @Convert(converter = MapConverter.class)
    @Column(name = "prop_value", length = 1000)
    private Map<String, String> props;

}
