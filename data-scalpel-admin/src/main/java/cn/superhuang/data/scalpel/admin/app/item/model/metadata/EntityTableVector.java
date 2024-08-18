package cn.superhuang.data.scalpel.admin.app.item.model.metadata;

import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import lombok.Data;

@Data
public class EntityTableVector extends EntityTable {
    private GeometryType geometryType;
    private Integer wkId;
    private Integer wk;
    private String extent;
}
