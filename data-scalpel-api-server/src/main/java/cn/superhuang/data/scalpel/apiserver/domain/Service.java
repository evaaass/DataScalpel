package cn.superhuang.data.scalpel.apiserver.domain;


import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Data
@Entity
@Table(name = "api_service")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Service extends AbstractAuditingEntity<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Schema(description = "服务类型：标准服务：STD,向导式：SQL，交互式：SCRIPT")
    private ServiceType type;
    private String name;
    private String uri;
    private String method;
    private String description;
    private String serviceDefinition;
    private String requestBody;
    private String requestBodyDefinition;
    private String responseBodyDefinition;
    private String responseBody;
    private String config;
    private String datasourceId;
}
