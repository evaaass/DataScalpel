package cn.superhuang.data.scalpel.admin.app.datasource.domain;

import cn.superhuang.data.scalpel.admin.app.datasource.model.enumeration.DatasourceCategory;
import cn.superhuang.data.scalpel.admin.app.datasource.model.enumeration.DatasourceState;
import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.admin.repository.converter.MapConverter;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
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

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "catalog_id")
    private String catalogId;

    @Enumerated(EnumType.STRING)
    @Column
    private DatasourceCategory category;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "alias", unique = true)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DatasourceType type;

    @Column(name = "full_type")
    private String fullType;

    @Convert(converter = MapConverter.class)
    @Column(name = "prop_value", length = 1000)
    private Map<String, String> props;

    @Schema(description = "数据源状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private DatasourceState state;

    @Schema(description = "异常信息")
    @Column(name = "state_detail")
    private String stateDetail;

    @Schema(description = "最后一次监测时间")
    @Column(name = "last_check_time")
    private Date lastCheckTime;
}
