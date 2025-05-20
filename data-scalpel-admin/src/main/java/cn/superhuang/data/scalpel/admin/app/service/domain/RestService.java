package cn.superhuang.data.scalpel.admin.app.service.domain;

import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceMethod;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceState;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "api_server_services")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RestService extends AbstractAuditingEntity<String> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7837805246932903874L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private String id;

    @Column
    private String catalogId;

    @Column
    private String engineId;

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
