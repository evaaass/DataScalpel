package util;


import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.Table;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.datasource.DatasourceItemColumnMetadata;
import cn.superhuang.data.scalpel.model.datasource.DatasourceItemMetadata;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.util.ScalaUtil;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.types.*;
import scala.Option;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchemaUtil {

    public static List<DataTableColumn> convertToDataTableColumn(StructType structType) {
        List<DataTableColumn> fields = new ArrayList<>();
        for (StructField structField : structType.fields()) {
            DataTableColumn field = new DataTableColumn();
            field.setName(structField.name());

            field.setType(getColumnType(null, structField.dataType(), null, null));
            if (structField.metadata().contains("precision")) {
                field.setPrecision((int) structField.metadata().getLong("precision"));
            }
            if (structField.metadata().contains("scale")) {
                field.setScale((int) structField.metadata().getLong("scale"));
            }
            if (structField.metadata().contains("cnName")) {
                field.setAlias(structField.metadata().getString("cnName"));
            } else {
                field.setAlias(structField.name());
            }
            fields.add(field);
        }
        return fields;
    }

    public static ColumnType getColumnType(DataType dataType, Metadata metadata) {
        return getColumnType(null, dataType, metadata, null);
    }

    public static ColumnType getColumnType(DbType dbType, DataType dataType, Metadata metadata, String originType) {
        if (originType != null && (originType.equalsIgnoreCase("geometry") || originType.equalsIgnoreCase("ST_GEOMETRY"))) {
            return ColumnType.GEOMETRY;
        }
        if ((dbType == DbType.MYSQL || dbType == DbType.OCEANBASE_MYSQL) && originType.equalsIgnoreCase("YEAR")) {
            return ColumnType.INTEGER;
        }
        //针对识别char类型时候打的补丁
        if (metadata != null && metadata.contains("__CHAR_VARCHAR_TYPE_STRING")) {
            String typeString = metadata.getString("__CHAR_VARCHAR_TYPE_STRING");
            DataType type = DataType.fromDDL(typeString);
            if (type instanceof CharType) {
                return ColumnType.FIXEDSTRING;
            }
        }

        for (ColumnType value : ColumnType.values()) {
            if (value.getSparkType().equals(dataType.typeName().replaceAll("\\([^\\)]+\\)", ""))) {
                return value;
            }
        }
        return ColumnType.STRING;
    }


    public static DatasourceItemMetadata getJdbcItemMetadata(Connection connection, JdbcConfig jdbcConfig, String tableName) throws ClassNotFoundException {
        DatasourceItemMetadata itemMetadata = new DatasourceItemMetadata();
        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());
        String catalog = DmpMetaUtil.getCataLog(connection);
        String schema = jdbcDialect.getSchema(jdbcConfig);
        String tableNameWithSchema = jdbcDialect.getTableWithSchema(tableName, jdbcConfig);
        String url = jdbcDialect.buildUrl(jdbcConfig);
        Map<String, String> options = new HashMap<>();
        options.put("driver", jdbcDialect.getDriver());
        scala.collection.immutable.Map scalaMap = ScalaUtil.convertToScalaImmutableMap(options);
        JDBCOptions jdbcOptions = new JDBCOptions(url, tableNameWithSchema, scalaMap);
        List<DatasourceItemColumnMetadata> itemColumnMetadataList = new ArrayList<>();
        Table table = DmpMetaUtil.getTableMeta(catalog, schema, connection, tableName);
        Option<StructType> structTypeOption = JdbcUtils.getSchemaOption(connection, jdbcOptions);
        if (!structTypeOption.isDefined()) {
            throw new RuntimeException("获取表" + tableName + "元数据失败，请检查账户权限");
        }
        StructType structType = structTypeOption.get();
        Map<String, Column> columnMap = table.getColumns().stream().collect(Collectors.toMap(Column::getName, c -> c));
        for (StructField field : structType.fields()) {
            DatasourceItemColumnMetadata itemColumnMetadata = new DatasourceItemColumnMetadata();
            itemColumnMetadata.setName(field.name());
            Column column = columnMap.get(field.name());                //TODO 处理，更精准的转换类型
            itemColumnMetadata.setOriginType(column.getTypeName().replaceAll(" UNSIGNED", ""));
            itemColumnMetadata.setType(SchemaUtil.getColumnType(jdbcConfig.getDbType(), field.dataType(), field.metadata(), column.getTypeName().replaceAll(" UNSIGNED", "")));
            if (StrUtil.isBlank(column.getComment())) {
                itemColumnMetadata.setCnName(column.getName());
            } else {
                itemColumnMetadata.setCnName(JdbcCommentUtil.getAliasFromComment(column.getComment()));
            }
            if (itemColumnMetadata.getType().getSupportPrecision()) {
                Integer precision = ((Long) column.getSize()).intValue();
                if (itemColumnMetadata.getType() == ColumnType.STRING && precision >= 2000) {
                    precision = 0;
                } else if (itemColumnMetadata.getType() == ColumnType.FIXEDSTRING && precision >= 2000) {
                    precision = 2000;
                } else if (itemColumnMetadata.getType() == ColumnType.DECIMAL && precision == 0 && column.getDigit() == 0) {
                    precision = 38;
                }
                itemColumnMetadata.setPrecision(precision);

            }
            if (itemColumnMetadata.getType().getSupportScale()) {
                itemColumnMetadata.setScale(column.getDigit());
            }
            itemColumnMetadata.setRemarks(JdbcCommentUtil.getDescriptionFromComment(column.getComment()));
            itemColumnMetadata.setIsPk(column.isPk());
            itemColumnMetadata.setNullable(column.isNullable());
            itemColumnMetadataList.add(itemColumnMetadata);
        }
        itemMetadata.setItem(table.getTableName());
        //TODO 这个工具类获取cnName
        itemMetadata.setAlias(JdbcCommentUtil.getAliasFromComment(table.getComment()));
        itemMetadata.setComment(JdbcCommentUtil.getDescriptionFromComment(table.getComment()));
        itemMetadata.setPkNames(table.getPkNames());
        itemMetadata.setColumns(itemColumnMetadataList);
        return itemMetadata;
    }
}
