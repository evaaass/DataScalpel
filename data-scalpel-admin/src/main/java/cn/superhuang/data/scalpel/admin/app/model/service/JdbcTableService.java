package cn.superhuang.data.scalpel.admin.app.model.service;

import cn.hutool.core.util.BooleanUtil;

import cn.superhuang.data.scalpel.admin.BaseException;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.util.ScalaUtil;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcOptionsInWrite;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.sedona_sql.UDT.GeometryUDT;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Service;
import scala.collection.JavaConverters;
import scala.collection.immutable.Seq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JdbcTableService {

    public void createTable(JdbcConfig jdbcConfig, String tableName, List<ModelField> fields) throws Exception {
        //TODO 把fields转成Schema

        StructType schema = new StructType();
        for (ModelField field : fields) {
            DataType dataType = getDataType(field);

            MetadataBuilder metadataBuilder = new MetadataBuilder();
            if (field.getPrecision() != null) {
                metadataBuilder = metadataBuilder.putLong("precision", field.getPrecision());
            }
            if (field.getScale() != null) {
                metadataBuilder = metadataBuilder.putLong("scale", field.getScale());
            }
            Boolean nullable = BooleanUtil.isTrue(field.getNullable());
            schema = schema.add(field.getName(), dataType, nullable, metadataBuilder.build());
        }


        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        String tableWithSchema = jdbcDialect.getTableWithSchema(tableName, jdbcConfig);


        JdbcOptionsInWrite jdbcOptionsInWrite = getSparkJdbcOptions(jdbcConfig, tableName);
        try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
            if (JdbcUtils.tableExists(conn, jdbcOptionsInWrite)) {
                throw new BaseException("表已经存在，请先删除表后再进行创建");
            }
            JdbcUtils.createTable(conn, tableWithSchema, schema, true, jdbcOptionsInWrite);
        }
    }

    public void dropTable(JdbcConfig jdbcConfig, String tableName) throws Exception {
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        JdbcOptionsInWrite jdbcOptionsInWrite = getSparkJdbcOptions(jdbcConfig, tableName);
        try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
            JdbcUtils.dropTable(conn, tableName, jdbcOptionsInWrite);
        }
    }

    public void updateTable(JdbcConfig jdbcConfig, String tableName, List<TableChange> tableChanges) throws Exception {
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        String tableWithSchema = jdbcDialect.getTableWithSchema(tableName, jdbcConfig);

        JdbcOptionsInWrite jdbcOptionsInWrite = getSparkJdbcOptions(jdbcConfig, tableName);
        try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
            Seq<TableChange> tableChangeSeq = JavaConverters.asScalaBuffer(tableChanges).toSeq();
            JdbcUtils.alterTable(conn, tableWithSchema, tableChangeSeq, jdbcOptionsInWrite);
        }
    }

    public List<TableChange> getTableChanges(List<ModelField> oldFields, List<ModelField> newFields) {
        Set<String> oldIdSet = oldFields.stream().map(f -> f.getId()).filter(f -> f != null).collect(Collectors.toSet());
        Set<String> newIdSet = newFields.stream().map(f -> f.getId()).filter(f -> f != null).collect(Collectors.toSet());

        Map<String, ModelField> oldFieldMap = oldFields.stream().collect(Collectors.toMap(f -> f.getId(), f -> f));

        //新的字段列表里面不存在旧ID时则为删除了
        List<TableChange> deleteColumnChanges = oldFields.stream().filter(f -> !newIdSet.contains(f.getId())).map(f -> {
            return TableChange.deleteColumn(new String[]{f.getName()}, false);
        }).collect(Collectors.toList());


        List<TableChange> renameColumnChanges = newFields.stream().filter(f -> f != null).filter(f -> {
            if (oldFieldMap.containsKey(f.getId())) {
                ModelField oldField = oldFieldMap.get(f.getId());
                if (!oldField.getName().equals(f.getName())) {
                    return true;
                }
            }
            return false;
        }).map(f -> {
            ModelField oldField = oldFieldMap.get(f.getId());
            return TableChange.renameColumn(new String[]{oldField.getName()}, f.getName());
        }).collect(Collectors.toList());

        List<TableChange> updateTypeColumnChanges = newFields.stream().filter(f -> f != null).filter(f -> {
            if (oldFieldMap.containsKey(f.getId())) {
                ModelField oldField = oldFieldMap.get(f.getId());
                if (!oldField.isTypeEqual(f)) {
                    return true;
                }
            }
            return false;
        }).map(f -> {
            return TableChange.updateColumnType(new String[]{f.getName()}, getDataType(f));
        }).collect(Collectors.toList());

        //新字段列表里面，如果ID为null，说明是要新增的
        List<TableChange> addColumnChanges = newFields.stream().filter(f -> f.getId() == null).map(f -> TableChange.addColumn(new String[]{f.getName()}, getDataType(f), f.getNullable(), f.getDescription())).collect(Collectors.toList());


        //顺序应该按:删除，修改，更新类型，新增来，避免冲突
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.addAll(deleteColumnChanges);
        tableChanges.addAll(renameColumnChanges);
        tableChanges.addAll(updateTypeColumnChanges);
        tableChanges.addAll(addColumnChanges);
        return tableChanges;
    }

    private DataType getDataType(ModelField field) {
        DataType dataType = null;
        if (field.getType() == ColumnType.GEOMETRY) {
            dataType = new GeometryUDT();
        } else if (field.getType() == ColumnType.DECIMAL) {
            dataType = DataType.fromDDL("decimal(" + field.getPrecision() + "," + field.getScale() + ")");
        } else if (field.getType() == ColumnType.FIXEDSTRING) {
            dataType = DataType.fromDDL("char(%d)".formatted(field.getPrecision()));
        } else if (field.getType() == ColumnType.STRING && (field.getPrecision() == 0 || field.getPrecision() > 1000)) {
            dataType = DataType.fromDDL("string");
        } else if (field.getType() == ColumnType.STRING) {
            dataType = DataType.fromDDL("varchar(%d)".formatted(field.getPrecision()));
        } else {
            dataType = DataType.fromDDL(field.getType().name());
        }
        return dataType;
    }

    private JdbcOptionsInWrite getSparkJdbcOptions(JdbcConfig jdbcConfig, String tableName) {
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());

        String tableWithSchema = jdbcDialect.getTableWithSchema(tableName, jdbcConfig);
        String driver = jdbcDialect.getDriver();
        String url = jdbcDialect.buildUrl(jdbcConfig);

        Map<String, String> options = new HashMap<>();
        options.put("url", url);
        options.put("driver", driver);
        options.put("username", jdbcConfig.getUsername());
        options.put("password", jdbcConfig.getPassword());
        options.put("dbtable", tableWithSchema);
        options.put("queryTimeout", "10000");

        JdbcOptionsInWrite jdbcOptionsInWrite = new JdbcOptionsInWrite(ScalaUtil.convertToScalaImmutableMap(options));
        return jdbcOptionsInWrite;
    }
}