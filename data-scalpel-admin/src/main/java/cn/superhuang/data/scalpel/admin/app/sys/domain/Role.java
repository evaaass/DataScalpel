package cn.superhuang.data.scalpel.admin.app.sys.domain;

import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.converter.ListConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "admin_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends AbstractAuditingEntity<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(unique = true)
    private String name;

    @Convert(converter = ListConverter.class)
    @Column(length = 1000)
    private List<String> permissions;

}
