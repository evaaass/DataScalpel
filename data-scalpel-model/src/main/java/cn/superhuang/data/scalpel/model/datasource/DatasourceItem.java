package cn.superhuang.data.scalpel.model.datasource;

import cn.superhuang.data.scalpel.model.enumeration.DatasourceItemType;
import lombok.Data;

@Data
public class DatasourceItem {
    private DatasourceItemType type;
    private String name;
    private String uri;
}
