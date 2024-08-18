package cn.superhuang.data.scalpel.model.datasource;

import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import lombok.Data;

@Data
public class DatasourceItemColumnMetadata {
    private String name;
    private String cnName;
    private ColumnType type;
    private String originType;
    private Integer precision;
    private Integer scale;
    private Boolean nullable;
    private Boolean isPk;
    private String remarks;
}
