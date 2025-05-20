package cn.superhuang.data.scalpel.admin.app.service.domain;

import cn.superhuang.data.scalpel.converter.MapConverter;
import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineState;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@Entity
@Table(name = "admin_service_engine")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ServiceEngine extends AbstractAuditingEntity<String> implements Serializable {
    @Serial
    private static final long serialVersionUID = 2967294105028169009L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private String id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private ServiceEngineType type;

    @Convert(converter = MapConverter.class)
    @Column(length = 1000)
    private Map<String, String> props;

    @Enumerated(EnumType.STRING)
    @Column
    private ServiceEngineState state;

    private String msg;
}
