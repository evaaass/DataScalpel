package cn.superhuang.data.scalpel.model.datasource.config;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JdbcConfig.class, name = "JDBC"),
        @JsonSubTypes.Type(value = S3Config.class, name = "S3"),
        @JsonSubTypes.Type(value = ApiConfig.class, name = "API"),
        @JsonSubTypes.Type(value = KafkaConfig.class, name = "KAFKA")
})

public abstract class DatasourceConfig {
    public static final String DS_COMMON_OPTIONS = "options";
    private DatasourceType type;
    private Map<String, String> params = new HashMap<>();

    public DatasourceType getType() {
        return type;
    }

    public void setType(DatasourceType type) {
        this.type = type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getOptions() {
        Map<String, String> map;
        if (params.containsKey(DS_COMMON_OPTIONS) && params.get(DS_COMMON_OPTIONS) != null) {
            String optionsJson = params.get(DS_COMMON_OPTIONS);
            map = JSONUtil.toBean(optionsJson, new TypeReference<Map<String, String>>() {
            }, false);
        } else {
            map = new HashMap<>();
        }
        return map;
    }

    public void putOption(String key, String value) {
        Map<String, String> options = getOptions();
        options.put(key, value);
        String optionsJson = JSONUtil.toJsonStr(options);
        params.put(DS_COMMON_OPTIONS, optionsJson);
    }

    public static DatasourceConfig getConfig(String type, String paramsContent) {
        DatasourceType typeEnum = DatasourceType.valueOf(type);
        Map<String, String> params = JSONUtil.toBean(paramsContent, new TypeReference<Map<String, String>>() {
        }, false);
        return getConfig(typeEnum, params);
    }

    public static DatasourceConfig getConfig(DatasourceType type, Map<String, String> params) {
        DatasourceConfig datasourceConfig = null;
        if (type == DatasourceType.JDBC) {
            datasourceConfig = new JdbcConfig();
        } else if (type == DatasourceType.S3) {
            datasourceConfig = new S3Config();
        } else if (type == DatasourceType.KAFKA) {
            datasourceConfig = new KafkaConfig();
        } else if (type == DatasourceType.API) {
            datasourceConfig = new ApiConfig();
        } else {
            throw new RuntimeException("不支持的数据类型：" + type);
        }
        datasourceConfig.setType(type);
        datasourceConfig.setParams(params);
        return datasourceConfig;
    }

}