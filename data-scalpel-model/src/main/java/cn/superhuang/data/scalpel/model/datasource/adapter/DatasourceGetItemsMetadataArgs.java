package cn.superhuang.data.scalpel.model.datasource.adapter;

import lombok.Data;

import java.util.List;

@Data
public class DatasourceGetItemsMetadataArgs {
    private List<DatasourceGetItemMetadataArgs> items;
}
