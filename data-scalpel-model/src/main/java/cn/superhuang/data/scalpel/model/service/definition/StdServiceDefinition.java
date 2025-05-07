package cn.superhuang.data.scalpel.model.service.definition;

import lombok.Data;

@Data
public class StdServiceDefinition extends BaseServiceDefinition {
    private String datasourceId;
    private String tableName;
}