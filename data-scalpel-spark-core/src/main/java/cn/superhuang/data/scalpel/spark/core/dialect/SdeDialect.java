package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;

import java.util.List;

public interface SdeDialect {
    public String getSdeBaseInfoQuery(String table);

    public String getSdeWktQuery(JdbcConfig config, String table, List<String> columns, String spatialColumn);
}
