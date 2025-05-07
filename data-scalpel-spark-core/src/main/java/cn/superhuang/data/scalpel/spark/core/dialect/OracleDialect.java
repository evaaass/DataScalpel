package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.connector.catalog.index.TableIndex;
import org.apache.spark.sql.connector.expressions.Expression;
import org.apache.spark.sql.connector.expressions.NamedReference;
import org.apache.spark.sql.connector.expressions.aggregate.AggregateFunc;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.execution.datasources.v2.TableSampleInfo;
import org.apache.spark.sql.jdbc.OracleDialect$;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.MetadataBuilder;
import scala.Function1;
import scala.Option;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;

public class OracleDialect extends DsJdbcDialect implements Serializable {


    @Serial
    private static final long serialVersionUID = -5414227420933222045L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.ORACLE;
    }

    @Override
    public String getDriver() {
        return "oracle.jdbc.OracleDriver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {

        java.util.Map<String, String> defaultParams = new HashMap<>();
        defaultParams.put("oracle.jdbc.J2EE13Compliant", "true");
        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        java.util.Map<String, String> params = config.getOptions();
        if (params != null && params.containsKey("CONNECTION_TYPE") && StrUtil.isNotBlank(params.get("CONNECTION_TYPE")) && params.get("CONNECTION_TYPE").equals("SERVICE_NAME")) {
            return StrUtil.format("jdbc:oracle:thin:@//{}:{}/{}{}", config.getHost(), config.getPort(), config.getDatabase(), extraParams);
        } else {
            return StrUtil.format("jdbc:oracle:thin:@{}:{}:{}{}", config.getHost(), config.getPort(), config.getDatabase(), extraParams);
        }
    }

    @Override
    public String getPreviewQuery(String table, Long limit, Long offset) {

        return """
                                
                SELECT * 
                FROM ( SELECT t.*, 
                       ROWNUM rn 
                       FROM %s t 
                       WHERE ROWNUM < %d )
                WHERE rn >=%d
                """.formatted(table, limit + offset, offset);
    }


    public String getDropTableQuery(String table, JdbcConfig config) {
        return String.format("drop table %s", table);
    }

    @Override
    public String getTableSizeQuery(String table, JdbcConfig config) {
        String schema = getSchema(config);
        return StrUtil.format("select sum(t.bytes) from dba_segments t where t.segment_type='TABLE' and t.OWNER='{}' and t.segment_name='{}' group by OWNER, t.segment_name, t.segment_type", schema, table);
    }
    private org.apache.spark.sql.jdbc.OracleDialect sparkDialect = OracleDialect$.MODULE$.apply();

    
    @Override
    public boolean canHandle(String url) {
        return sparkDialect.canHandle(url);
    }
    

    @Override
    public boolean isTraceEnabled() {
        return sparkDialect.isTraceEnabled();
    }

    @Override
    public void initializeLogIfNecessary(boolean isInterpreter) {
        sparkDialect.initializeLogIfNecessary(isInterpreter);
    }

    @Override
    public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
        return sparkDialect.initializeLogIfNecessary(isInterpreter, silent);
    }

    @Override
    public void initializeForcefully(boolean isInterpreter, boolean silent) {
        sparkDialect.initializeForcefully(isInterpreter, silent);
    }

    @Override
    public Option<DataType> getCatalystType(int sqlType, String typeName, int size, MetadataBuilder md) {
        if (sqlType == Types.ARRAY) {
            return Option.apply(DataTypes.NullType);
        } else if (sqlType == Types.FLOAT) {
            return Option.apply(DataTypes.FloatType);
        } else if (sqlType == Types.DOUBLE) {
            return Option.apply(DataTypes.DoubleType);
        } else {
            return sparkDialect.getCatalystType(sqlType, typeName, size, md);
        }
    }

    @Override
    public Option<org.apache.spark.sql.jdbc.JdbcType> getJDBCType(DataType dt) {
        if (dt == DataTypes.StringType) {
            org.apache.spark.sql.jdbc.JdbcType jdbcType = new org.apache.spark.sql.jdbc.JdbcType("VARCHAR2(4000)", Types.VARCHAR);
            return Option.apply(jdbcType);
        } else {
            return sparkDialect.getJDBCType(dt);
        }

    }

    @Override
    public Function1<Object, Connection> createConnectionFactory(JDBCOptions options) {
        return sparkDialect.createConnectionFactory(options);
    }

    @Override
    public String quoteIdentifier(String colName) {
        return sparkDialect.quoteIdentifier(colName);
    }

    @Override
    public String getTableExistsQuery(String table) {
        return sparkDialect.getTableExistsQuery(table);
    }

    @Override
    public String getSchemaQuery(String table) {
        return sparkDialect.getSchemaQuery(table);
    }

    @Override
    public String getTruncateQuery(String table) {
        return sparkDialect.getTruncateQuery(table);
    }

    @Override
    public String getTruncateQuery(String table, Option<Object> cascade) {
        return sparkDialect.getTruncateQuery(table, cascade);
    }

    @Override
    public void beforeFetch(Connection connection, Map<String, String> properties) {
        sparkDialect.beforeFetch(connection, properties);
    }

    @Override
    public String escapeSql(String value) {
        return sparkDialect.escapeSql(value);
    }

    @Override
    public Object compileValue(Object value) {
        return sparkDialect.compileValue(value);
    }

    @Override
    public boolean isSupportedFunction(String funcName) {
        return sparkDialect.isSupportedFunction(funcName);
    }

    @Override
    public Option<String> compileExpression(Expression expr) {
        return sparkDialect.compileExpression(expr);
    }

    @Override
    public Option<String> compileAggregate(AggregateFunc aggFunction) {
        return sparkDialect.compileAggregate(aggFunction);
    }
    

    @Override
    public void createSchema(Statement statement, String schema, String comment) {
        sparkDialect.createSchema(statement, schema, comment);
    }

    @Override
    public boolean schemasExists(Connection conn, JDBCOptions options, String schema) {
        return sparkDialect.schemasExists(conn, options, schema);
    }

    @Override
    public String[][] listSchemas(Connection conn, JDBCOptions options) {
        return sparkDialect.listSchemas(conn, options);
    }

    @Override
    public Option<Object> isCascadingTruncateTable() {
        return sparkDialect.isCascadingTruncateTable();
    }

    @Override
    public String renameTable(String oldTable, String newTable) {
        return sparkDialect.renameTable(oldTable, newTable);
    }

    @Override
    public String[] alterTable(String tableName, Seq<TableChange> changes, int dbMajorVersion) {
        return sparkDialect.alterTable(tableName, changes, dbMajorVersion);
    }

    @Override
    public String getAddColumnQuery(String tableName, String columnName, String dataType) {
        return sparkDialect.getAddColumnQuery(tableName, columnName, dataType);
    }

    @Override
    public String getRenameColumnQuery(String tableName, String columnName, String newName, int dbMajorVersion) {
        return sparkDialect.getRenameColumnQuery(tableName, columnName, newName, dbMajorVersion);
    }

    @Override
    public String getDeleteColumnQuery(String tableName, String columnName) {
        return sparkDialect.getDeleteColumnQuery(tableName, columnName);
    }

    @Override
    public String getUpdateColumnTypeQuery(String tableName, String columnName, String newDataType) {
        return sparkDialect.getUpdateColumnTypeQuery(tableName, columnName, newDataType);
    }

    @Override
    public String getUpdateColumnNullabilityQuery(String tableName, String columnName, boolean isNullable) {
        return sparkDialect.getUpdateColumnNullabilityQuery(tableName, columnName, isNullable);
    }

    @Override
    public String getTableCommentQuery(String table, String comment) {
        return sparkDialect.getTableCommentQuery(table, comment);
    }

    @Override
    public String getSchemaCommentQuery(String schema, String comment) {
        return sparkDialect.getSchemaCommentQuery(schema, comment);
    }

    @Override
    public String removeSchemaCommentQuery(String schema) {
        return sparkDialect.removeSchemaCommentQuery(schema);
    }

    @Override
    public String dropSchema(String schema, boolean cascade) {
        return sparkDialect.dropSchema(schema, cascade);
    }

    @Override
    public String createIndex(String indexName, Identifier tableIdent, NamedReference[] columns, java.util.Map<NamedReference, java.util.Map<String, String>> columnsProperties, java.util.Map<String, String> properties) {
        return sparkDialect.createIndex(indexName, tableIdent, columns, columnsProperties, properties);
    }

    @Override
    public boolean indexExists(Connection conn, String indexName, Identifier tableIdent, JDBCOptions options) {
        return sparkDialect.indexExists(conn, indexName, tableIdent, options);
    }

    @Override
    public String dropIndex(String indexName, Identifier tableIdent) {
        return sparkDialect.dropIndex(indexName, tableIdent);
    }

    @Override
    public TableIndex[] listIndexes(Connection conn, Identifier tableIdent, JDBCOptions options) {
        return sparkDialect.listIndexes(conn, tableIdent, options);
    }

    @Override
    public AnalysisException classifyException(String message, Throwable e) {
        return sparkDialect.classifyException(message, e);
    }

    @Override
    public String getLimitClause(Integer limit) {
        return sparkDialect.getLimitClause(limit);
    }

    @Override
    public String getOffsetClause(Integer offset) {
        return sparkDialect.getOffsetClause(offset);
    }

    @Override
    public boolean supportsTableSample() {
        return sparkDialect.supportsTableSample();
    }

    @Override
    public String getTableSample(TableSampleInfo sample) {
        return sparkDialect.getTableSample(sample);
    }

}
