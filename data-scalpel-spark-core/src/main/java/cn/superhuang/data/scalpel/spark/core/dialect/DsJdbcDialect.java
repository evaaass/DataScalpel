package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.hutool.core.util.StrUtil;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.change.DmpAddPkChange;
import cn.superhuang.data.scalpel.spark.core.dialect.change.DmpDropPkChange;
import cn.superhuang.data.scalpel.spark.core.dialect.change.DmpUpdateColumnComment;
import com.querydsl.sql.SQLTemplates;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.jdbc.JdbcDialect;
import scala.collection.immutable.Seq;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public abstract class DsJdbcDialect extends JdbcDialect implements Serializable {

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
        return new HashMap<>();
    }

    public Map<String, String> getSparkExtraOptions() {
        return new HashMap<>();
    }

    public String getAddTablePrimaryKeyQuery(String tableName, List<String> pkList) {
        throw new RuntimeException("暂不支持这种操作");
    }

    ;

    public String getDropTablePrimaryKeyQuery(String tableName, List<String> pkList) {
        throw new RuntimeException("暂不支持这种操作");
    }

    ;

    public String getUpdateTableCommentQuery(String tableName, String comment) {
        throw new RuntimeException("暂不支持这种操作");
    }

    ;

    public String getUpdateColumnCommentQuery(String tableName, String column, String type, String comment, Boolean nullable) {
        throw new RuntimeException("暂不支持这种操作");
    }

    ;

    public String getUpdateColumnNullableTQuery(String tableName, String column, String type, String comment, Boolean nullable) {
        throw new RuntimeException("暂不支持这种操作");
    }

    ;

    @Override
    public String[] alterTable(String tableName, Seq<TableChange> changes, int dbMajorVersion) {
        List<String> updateClause = new ArrayList<>();
        for (TableChange change : (List<TableChange>) scala.jdk.CollectionConverters.SeqHasAsJava(changes).asJava()) {
            if (change instanceof TableChange.AddColumn) {
                TableChange.AddColumn add = (TableChange.AddColumn) change;
                if (add.fieldNames().length == 1) {
                    String dataType = JdbcUtils.getJdbcType(add.dataType(), this).databaseTypeDefinition();
                    String name = add.fieldNames()[0];
                    updateClause.add(getAddColumnQuery(tableName, name, dataType));
                }
            } else if (change instanceof TableChange.RenameColumn) {
                TableChange.RenameColumn rename = (TableChange.RenameColumn) change;
                if (rename.fieldNames().length == 1) {
                    String name = rename.fieldNames()[0];
                    updateClause.add(getRenameColumnQuery(tableName, name, rename.newName(), dbMajorVersion));
                }
            } else if (change instanceof TableChange.DeleteColumn) {
                TableChange.DeleteColumn delete = (TableChange.DeleteColumn) change;
                if (delete.fieldNames().length == 1) {
                    String name = delete.fieldNames()[0];
                    updateClause.add(getDeleteColumnQuery(tableName, name));
                }
            } else if (change instanceof TableChange.UpdateColumnType) {
                TableChange.UpdateColumnType updateColumnType = (TableChange.UpdateColumnType) change;
                if (updateColumnType.fieldNames().length == 1) {
                    String name = updateColumnType.fieldNames()[0];
                    String dataType = JdbcUtils.getJdbcType(updateColumnType.newDataType(), this).databaseTypeDefinition();
                    updateClause.add(getUpdateColumnTypeQuery(tableName, name, dataType));
                }
            } else if (change instanceof TableChange.UpdateColumnNullability) {
                TableChange.UpdateColumnNullability updateNull = (TableChange.UpdateColumnNullability) change;
                if (updateNull.fieldNames().length == 1) {
                    String name = updateNull.fieldNames()[0];
                    updateClause.add(getUpdateColumnNullabilityQuery(tableName, name, updateNull.nullable()));
                }
            } else if (change instanceof DmpUpdateColumnComment) {
                DmpUpdateColumnComment updateColumnComment = (DmpUpdateColumnComment) change;
                if (updateColumnComment.fieldNames().length == 1) {
                    String name = updateColumnComment.fieldNames()[0];
                    updateClause.add(getUpdateColumnCommentQuery(tableName, name, JdbcUtils.getJdbcType(updateColumnComment.getType(), this).databaseTypeDefinition(), updateColumnComment.getNewComment(), updateColumnComment.getNullable()));
                }
            } else if (change instanceof DmpDropPkChange) {
                DmpDropPkChange dropPkChange = (DmpDropPkChange) change;
                updateClause.add(getDropTablePrimaryKeyQuery(tableName, Arrays.asList(dropPkChange.fieldNames())));
            } else if (change instanceof DmpAddPkChange) {
                DmpAddPkChange addPkChange = (DmpAddPkChange) change;
                updateClause.add(getAddTablePrimaryKeyQuery(tableName, Arrays.asList(addPkChange.fieldNames())));
            } else {
                throw new UnsupportedOperationException(
                        "Unsupported table change in JDBC Catalog: " + change.getClass().getName());
            }
        }

        return updateClause.toArray(new String[0]);
    }

    public SQLTemplates getSQLTemplates() {
        return SQLTemplates.DEFAULT;
    }
}
