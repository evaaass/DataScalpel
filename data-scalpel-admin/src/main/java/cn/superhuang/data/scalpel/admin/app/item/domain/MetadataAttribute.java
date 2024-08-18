package cn.superhuang.data.scalpel.admin.app.item.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class MetadataAttribute {
    private String name;
    private String typeName;
}
