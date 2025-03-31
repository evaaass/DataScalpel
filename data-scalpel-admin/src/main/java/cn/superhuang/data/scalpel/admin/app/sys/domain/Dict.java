package cn.superhuang.data.scalpel.admin.app.sys.domain;

import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import cn.superhuang.data.scalpel.converter.MapConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Entity
@Table(name = "admin_dict")
public class Dict extends AbstractAuditingEntity<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    private String type;

    private String name;

    private String value;

    private String description;

    private String parentId;

    @Convert(converter = MapConverter.class)
    @Column(length = 1000)
    private Map<String, Object> options;
}
