package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class TDEngineRSDialect extends SysJdbcDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = 3611418504839864962L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.TD_ENGINE_RS;
    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:TAOS-RS:");
    }

    @Override
    public String quoteIdentifier(String colName) {
        return colName;
    }

    @Override
    public String getTableExistsQuery(String table) {
        return "SELECT * FROM " + table + " limit 0";
    }

    @Override
    public String getSchemaQuery(String table) {
        return "SELECT * FROM " + table + " limit 0,1";
    }

    @Override
    public String getDriver() {
        return "com.taosdata.jdbc.rs.RestfulDriver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        java.util.Map<String, String> defaultParams = new HashMap<>();

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        return StrUtil.format("jdbc:TAOS-RS://{}:{}/{}{}", config.getHost(), config.getPort(), config.getDatabase(), extraParams);
    }

    @Override
    public String getPreviewQuery(String table, Long limit, Long offset) {
        return "SELECT * FROM %s limit %d,%d".formatted(table, offset, limit);
    }

    @Override
    public String getTableSizeQuery(String table, JdbcConfig config) {
        return StrUtil.format("select _block_dist() from {}.{}", config.getDatabase(), table);
    }
}
