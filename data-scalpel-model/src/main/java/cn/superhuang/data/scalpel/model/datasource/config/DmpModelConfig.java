package cn.superhuang.data.scalpel.model.datasource.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"params"})
public class DmpModelConfig extends DatasourceConfig {
    public static final String DMP_MODEL_LAYER = "LayerType";

    public String getDmpModelDsId() {
        return getParams().get(DMP_MODEL_LAYER);
    }

    public void setAccessId(String datasourceId) {
        getParams().put(DMP_MODEL_LAYER, datasourceId);
    }
}
