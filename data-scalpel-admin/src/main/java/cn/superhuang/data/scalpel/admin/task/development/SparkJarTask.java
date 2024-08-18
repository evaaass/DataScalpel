package cn.superhuang.data.scalpel.admin.task.development;

import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.item.domain.LakeItem;

import java.util.List;
import java.util.Map;

public class SparkJarTask {

    private List<String> sourceItems;
    private List<String> sinkItems;

    
    private Map<String, Datasource> datasourceMap;
    private Map<String, LakeItem> lakeItemMap;
}