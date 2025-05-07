package cn.superhuang.data.scalpel.spark.core.dialect;


import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class ClickhouseDialect extends DsJdbcDialect implements Serializable {

    @Serial
    private static final long serialVersionUID = -8905684848521642218L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.CLICKHOUSE;
    }

    @Override
    public String getDriver() {
        return "com.clickhouse.jdbc.ClickHouseDriver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        java.util.Map<String, String> defaultParams = new HashMap<>();

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        String db = "";
        if (StrUtil.isNotBlank(config.getDatabase())) {
            db = "/" + config.getDatabase();
        }
        return StrUtil.format("jdbc:clickhouse://{}:{}{}{}", config.getHost(), config.getPort(), db, extraParams);

    }

    @Override
    public String getPreviewQuery(String table, Long limit, Long offset) {
        return StrUtil.format("select * from {} limit {},{}", table, offset, limit);
    }

    @Override
    public String getTableSizeQuery(String table, JdbcConfig config) {

        //      获取行数直接用这个，看看要不要改.. sum(rows) as rows
        String sql = """
                select
                       sum(bytes) as size
                  from system.parts
                 where active
                   and database = '%s'
                   and table = '%s'
                 group by database, table
                """;
        return sql.formatted(config.getDatabase(), table);
    }

    public String getDropTableQuery(String table, JdbcConfig config) {
        String tableWithSchema = getTableWithSchema(table, config);
        String clusterName = config.getOptions().get(JdbcConfig.JDBC_OPTIONS_KEY_CK_CLUSTER_NAME);
        if (StrUtil.isNotBlank(clusterName)) {
            return "drop table %s_physical on cluster &s".formatted(tableWithSchema, clusterName);
        } else {
            return "drop table %s".formatted(tableWithSchema);
        }
    }

    @Override
    public String getTableDeleteQuery(String table, String condition, JdbcConfig config) {
        String clusterName = config.getOptions().get(JdbcConfig.JDBC_OPTIONS_KEY_CK_CLUSTER_NAME);
        if (StrUtil.isNotBlank(clusterName)) {
            return "ALTER TABLE %s_physical ON CLUSTER %s DELETE WHERE %s".formatted(table, condition, condition);
        } else {
            return super.getTableDeleteQuery(table, condition, config);
        }
    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:clickhouse:");
    }


}
