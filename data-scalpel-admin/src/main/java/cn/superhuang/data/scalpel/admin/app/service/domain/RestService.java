package cn.superhuang.data.scalpel.admin.app.service.domain;

import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceMethod;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceState;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Data
@Entity
@Table(name = "api-server-services")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RestService extends AbstractAuditingEntity<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "catalog_id")
    private String catalogId;

    @Enumerated(EnumType.STRING)
    @Column
    private RestServiceType type;

    @Enumerated(EnumType.STRING)
    @Column
    private RestServiceMethod method;

    private String uri;

    @Column(name = "name", unique = true)
    private String name;

    @Column
    private String description;

    private String serviceDefinition;
    @Column(columnDefinition = "TEXT")
    private String requestDefinition;
    @Column(columnDefinition = "TEXT")
    private String responseDefinition;
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private RestServiceState state;
}
