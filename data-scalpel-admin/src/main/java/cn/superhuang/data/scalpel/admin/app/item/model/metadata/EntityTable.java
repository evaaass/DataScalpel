package cn.superhuang.data.scalpel.admin.app.item.model.metadata;

import lombok.Data;

import java.util.List;

@Data
public class EntityTable {
    private String dataTypeId;
    private String standardSpecificationId;
    private List<StructColumn> columns;
}
