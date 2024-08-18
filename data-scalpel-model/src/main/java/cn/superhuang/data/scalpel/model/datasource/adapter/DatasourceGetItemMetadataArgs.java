package cn.superhuang.data.scalpel.model.datasource.adapter;

import lombok.Data;

import java.util.Map;

@Data
public class DatasourceGetItemMetadataArgs {
    private String item;
    private Map<String, String> params;
}
