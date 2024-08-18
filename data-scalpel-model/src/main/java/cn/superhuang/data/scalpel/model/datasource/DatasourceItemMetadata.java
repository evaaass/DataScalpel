package cn.superhuang.data.scalpel.model.datasource;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DatasourceItemMetadata {
    private String item;
    private String comment;
    private Set<String> pkNames;
    private List<DatasourceItemColumnMetadata> columns;
}
