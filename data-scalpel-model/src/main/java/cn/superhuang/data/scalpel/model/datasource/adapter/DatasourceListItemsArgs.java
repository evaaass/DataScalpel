package cn.superhuang.data.scalpel.model.datasource.adapter;

import cn.superhuang.data.scalpel.model.enumeration.DatasourceItemType;
import lombok.Data;

import java.util.Map;

@Data
public class DatasourceListItemsArgs {
    private DatasourceItemType type;
    private String parentUri;
    private Map<String, String> params;
}
