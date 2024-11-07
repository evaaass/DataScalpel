package cn.superhuang.data.scalpel.admin.app.service.impl.adaptor;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.Table;
import cn.hutool.db.sql.SqlExecutor;
import cn.superhuang.data.scalpel.admin.app.datasource.DsAdaptorException;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.*;
import cn.superhuang.data.scalpel.admin.util.MetaUtil;
import cn.superhuang.data.scalpel.admin.util.SchemaUtil;
import cn.superhuang.data.scalpel.admin.util.StringUtil;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.util.ScalaUtil;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JdbcAdaptor extends BaseDsAdaptor {


    @Override
    public Boolean canHandle(DatasourceConfig datasourceConfig) {
        return datasourceConfig instanceof JdbcConfig;
    }

    @Override
    public Boolean supportBatch() {
        return true;
    }

    @Override
    public Boolean supportStream() {
        return false;
    }

    @Override
    public Boolean supportCatalog() {
        return false;
    }

    @Override
    public DsCheckResult check(DatasourceConfig datasourceConfig) {
        DsCheckResult result = new DsCheckResult();
        try {
            JdbcConfig jdbcConfig = (JdbcConfig) datasourceConfig;
            SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
            Class.forName(jdbcDialect.getDriver());
            try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(e));
            result.setDetail(ExceptionUtil.stacktraceToString(e));
        }
        return result;
    }

    @Override
    public List<DsItem> listItem(DatasourceConfig datasourceConfig, DsListItemArgs listItemArgs) {
        JdbcConfig jdbcConfig = (JdbcConfig) datasourceConfig;
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        try {
            Class.forName(jdbcDialect.getDriver());
            try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
                List<DsItem> items = new ArrayList<>();

                DatabaseMetaData databaseMetaData = conn.getMetaData();
                ResultSet tableRs = databaseMetaData.getTables(conn.getCatalog(), jdbcDialect.getSchema(jdbcConfig), null, new String[]{"TABLE"});
                while (tableRs.next()) {
                    String tableName = tableRs.getString("TABLE_NAME");
                    String remarks = tableRs.getString("REMARKS");

                    //TDEngine需要特殊处理
                    if ((jdbcConfig.getDbType() == DbType.TD_ENGINE || jdbcConfig.getDbType() == DbType.TD_ENGINE_RS) && !StrUtil.equals(remarks, "STABLE")) {
                        continue;
                    }
                    DsItem item = new DsItem();
                    item.setType(DsItemType.JDBC_TABLE);
                    item.setName(tableName);
                    item.setDescription(remarks);
                    items.add(item);
                }
                return items;
            }
        } catch (Exception e) {
            throw new DsAdaptorException("获取item失败:" + e.getMessage(), e);
        }
    }


    @Override
    public DsItemMetadata getItemMetadata(DatasourceConfig datasourceConfig, DsGetItemMetadataArgs getItemMetadataArgs) {
        JdbcConfig jdbcConfig = (JdbcConfig) datasourceConfig;
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        try {
            Class.forName(jdbcDialect.getDriver());
            DsItemMetadata dsItemMetadata = new DsItemMetadata();
            try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
                String tableName = jdbcDialect.getTableWithSchema(getItemMetadataArgs.getItem(), jdbcConfig);

                Map<String, String> options = new HashMap<>();
                scala.collection.immutable.Map scalaMap = ScalaUtil.convertToScalaImmutableMap(options);
                JDBCOptions jdbcOptions = new JDBCOptions(jdbcDialect.buildUrl(jdbcConfig), tableName, scalaMap);
                StructType structType = JdbcUtils.getSchemaOption(conn, jdbcOptions).get();

                Table table = MetaUtil.getTableMeta(conn.getCatalog(), jdbcDialect.getSchema(jdbcConfig), conn, getItemMetadataArgs.getItem());
                Map<String, Column> columnMap = table.getColumns().stream().collect(Collectors.toMap(Column::getName, c -> c));
                List<DataTableColumn> columns = new ArrayList<>();
                for (StructField field : structType.fields()) {
                    Column column = columnMap.get(field.name());
                    DataTableColumn dataTableColumn = new DataTableColumn();
                    dataTableColumn.setOriginType(column.getTypeName());
                    dataTableColumn.setName(field.name());
                    dataTableColumn.setAlias(StringUtil.getNameFromRemark(column.getComment()));
                    dataTableColumn.setRemark(column.getComment());
                    dataTableColumn.setType(SchemaUtil.getColumnType(field.dataType(), column.getTypeName().replaceAll(" UNSIGNED", "")));
                    if (field.metadata().contains("precision")) {
                        dataTableColumn.setPrecision((int) field.metadata().getLong("precision"));
                    }
                    if (field.metadata().contains("scale")) {
                        dataTableColumn.setScale((int) field.metadata().getLong("scale"));
                    }
                    columns.add(dataTableColumn);
                }

                DataTable dataTable = new DataTable();
                dataTable.setName(table.getTableName());
                dataTable.setAlias(table.getComment());
                dataTable.setColumns(columns);
                dataTable.setMetadata(new HashMap<>());

                dsItemMetadata.setTable(dataTable);
                return dsItemMetadata;
            }
        } catch (Exception e) {
            throw new DsAdaptorException("获取item失败:" + e.getMessage(), e);
        }
    }

    @Override
    public DsItemPreviewResult getItemPreviewData(DatasourceConfig datasourceConfig, DsGetItemPreviewDataArgs getItemPreviewDataArgs) {
        JdbcConfig jdbcConfig = (JdbcConfig) datasourceConfig;
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        try {
            try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
                String tableName = jdbcDialect.getTableWithSchema(getItemPreviewDataArgs.getItem(), jdbcConfig);
                String querySql = jdbcDialect.getPreviewQuery(tableName, 50l, 0l);
                String countSql = jdbcDialect.getTableCountQuery(tableName);

                List<Map> records = new ArrayList<>();
                Number countNumber = SqlExecutor.query(conn, countSql, new NumberHandler());
                List<Entity> recordEntities = SqlExecutor.query(conn, querySql, new EntityListHandler());
                for (Entity recordEntity : recordEntities) {
                    Map<String, String> map = new HashMap<>();
                    for (String fieldName : recordEntity.getFieldNames()) {
                        map.put(fieldName, recordEntity.get(fieldName) == null ? null : recordEntity.get(fieldName).toString());
                    }
                    records.add(map);
                }

                DsItemPreviewResult itemPreview = new DsItemPreviewResult();
                itemPreview.setCount(countNumber.longValue());
                itemPreview.setContent(records);
                return itemPreview;
            }
        } catch (Exception e) {
            throw new DsAdaptorException("获取item失败:" + e.getMessage(), e);
        }
    }

    public ColumnType getColumnType(String type, String originType) {
        if (originType.equalsIgnoreCase("geometry") || originType.equalsIgnoreCase("ST_GEOMETRY")) {
            return ColumnType.GEOMETRY;
        }
        for (ColumnType value : ColumnType.values()) {
            if (value.getSparkType().equals(type.replaceAll("\\([^\\)]+\\)", ""))) {
                return value;
            }
        }
        return ColumnType.STRING;
    }
}
