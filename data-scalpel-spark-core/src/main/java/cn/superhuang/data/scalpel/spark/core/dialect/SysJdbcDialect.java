package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.hutool.core.util.StrUtil;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import com.querydsl.sql.SQLTemplates;
import org.apache.spark.sql.jdbc.JdbcDialect;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class SysJdbcDialect extends JdbcDialect implements Serializable {

    @Serial
    private static final long serialVersionUID = 503507589088087335L;

    public abstract Boolean canHandle(DbType type);

    public abstract String getDriver();

    public abstract String buildUrl(JdbcConfig jdbcConfig);

    public abstract String getPreviewQuery(String table, Long limit, Long offset);

    //这个返回得schema是没quoteIdentifier的
    public String getSchema(JdbcConfig config) {
        String schema = config.getSchema();
        return schema;
    }

    //这个返回得是quoteIdentifier过的
    public String getTableWithSchema(String table, JdbcConfig config) {
        String schema = getSchema(config);
        if (schema != null) {
            schema = quoteIdentifier(schema) + ".";
        } else {
            schema = "";
        }
        return schema + quoteIdentifier(table);
    }

    public String getTableWithSchema(String table, String schema) {
        if (schema != null) {
            schema = quoteIdentifier(schema) + ".";
        } else {
            schema = "";
        }
        return schema + quoteIdentifier(table);
    }

    //这个方法的table需要带上schema
    public String getTableCountQuery(String tableWithSchema) {
        return StrUtil.format("select count(*) from {}", tableWithSchema);
    }

    public String getTableCountQuery(String table, String condition) {
        return "SELECT COUNT(1) FROM %s WHERE %s".formatted(table, condition);
    }

    public String getDropTableQuery(String table, JdbcConfig jdbcConfig) {
        return String.format("drop table if exists %s", table);
    }

    //这个table，别带上schema
    public String getTableSizeQuery(String table, JdbcConfig jdbcConfig) {
        throw new RuntimeException("暂不支持查询该类型数据库表占用空间：" + jdbcConfig.getDbType());
    }

    /**
     * @param table
     * @param condition
     * @param jdbcConfig 这个参数只有CK的情况需要
     * @return
     */
    public String getTableDeleteQuery(String table, String condition, JdbcConfig jdbcConfig) {
        return "DELETE FROM %s WHERE %s".formatted(table, condition);
    }

    public String getTableInsertQuery(String table, String insertFieldNames, String insertValues) {
        return "INSERT INTO %s (%s) VALUES (%s)".formatted(table, insertFieldNames, insertValues);
    }

    public String getTableUpdateQuery(String table, String updateFieldValues, String condition) {
        return "UPDATE %s SET %s WHERE %s".formatted(table, updateFieldValues, condition);
    }

    public Map<String, String> getSparkJdbcOptions(String dbTable, JdbcConfig jdbcConfig) {
        Map<String, String> options = new HashMap<>();
        options.put("driver", getDriver());
        options.put("url", buildUrl(jdbcConfig));
        options.put("dbtable", dbTable);
        options.put("user", jdbcConfig.getUsername());
        options.put("password", jdbcConfig.getPassword());
        return options;
    }

    public Map<String, String> getSparkExtraOptions() {
        return new HashMap<>();
    }


    public SQLTemplates getSQLTemplates() {
        return SQLTemplates.DEFAULT;
    }
}
