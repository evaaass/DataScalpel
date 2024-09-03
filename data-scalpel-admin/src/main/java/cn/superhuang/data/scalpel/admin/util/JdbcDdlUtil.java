package cn.superhuang.data.scalpel.admin.util;

import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.spark.core.dialect.change.DmpAddPkChange;
import cn.superhuang.data.scalpel.spark.core.dialect.change.DmpDropPkChange;
import cn.superhuang.data.scalpel.spark.core.dialect.change.DmpUpdateColumnComment;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.sedona_sql.UDT.GeometryUDT;
import org.apache.spark.sql.types.DataType;
import scala.collection.JavaConverters;
import scala.collection.immutable.Seq;

import java.util.*;
import java.util.stream.Collectors;

public class JdbcDdlUtil {

    public static String[] getCreateTableSql(SysJdbcDialect dialect, Model model, List<ModelField> fields) {
        String schemaString = schemaString(dialect, fields);
        //TODO 后面用来扩展...
        String createTableOptions = "";
        List<String> sqlList = new ArrayList<>();
        String createTableSql = StrUtil.format("CREATE TABLE {} ({}) {}", dialect.quoteIdentifier(model.getName()),
                schemaString, createTableOptions);
        String updateTableCommentSql = dialect.getUpdateTableCommentQuery(dialect.quoteIdentifier(model.getName()), model.getDescription());

        List<String> updateColumnSqlList = fields.stream().map(f -> {
            String typeStr = JdbcUtils.getJdbcType(getDataType(f), dialect).databaseTypeDefinition();
            return dialect.getUpdateColumnCommentQuery(
                    dialect.quoteIdentifier(model.getName())
                    , dialect.quoteIdentifier(f.getName())
                    , typeStr
                    , f.getDescription(), f.getNullable());
        }).collect(Collectors.toList());

        List<String> pkList = fields.stream().filter(ModelField::getPrimaryKey).map(f -> dialect.quoteIdentifier(f.getName())).collect(Collectors.toList());
        sqlList.add(createTableSql);
        if (!pkList.isEmpty()) {
            String addPkSql = dialect.getAddTablePrimaryKeyQuery(dialect.quoteIdentifier(model.getName()), pkList);
            sqlList.add(addPkSql);
        }
        sqlList.add(updateTableCommentSql);
        sqlList.addAll(updateColumnSqlList);
        return sqlList.toArray(sqlList.toArray(new String[0]));
    }

    public static String[] getDropTableSql(SysJdbcDialect dialect, Model model) {
        String dropSql = "DROP TABLE " + dialect.quoteIdentifier(model.getName());
        return new String[]{dropSql};
    }

    public static String[] getUpdateTableSql(SysJdbcDialect dialect, String tableName, List<ModelField> oldFields,
                                             List<ModelField> newFields, Integer dbMajorVersion) {
        List<TableChange> tableChanges = getTableChanges(oldFields, newFields);
        Seq<TableChange> tableChangeSeq = JavaConverters.asScalaIteratorConverter(tableChanges.iterator()).asScala()
                .toSeq();
        String[] sqlArr = dialect.alterTable(tableName, tableChangeSeq, dbMajorVersion);
        return sqlArr;
    }


    public static String schemaString(SysJdbcDialect dialect, List<ModelField> fields) {
        return fields.stream().map(field -> {
            String name = field.getName();
            String nullableStr = field.getNullable() ? "" : "NOT NULL";
            String typeStr = JdbcUtils.getJdbcType(getDataType(field), dialect).databaseTypeDefinition();
            return StrUtil.format(" {} {} {}", name, typeStr, nullableStr);
        }).collect(Collectors.joining(","));
    }

    public static List<TableChange> getTableChanges(List<ModelField> oldFields, List<ModelField> newFields) {
        //        Set<Long> oldIdSet = oldFields.stream().map(DataModelField::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<TableChange> tableChanges = new ArrayList<>();
        Set<String> oldIdSet = oldFields.stream().map(ModelField::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> newIdSet = newFields.stream().map(ModelField::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, ModelField> oldFieldMap = oldFields.stream().collect(Collectors.toMap(ModelField::getId, f -> f));    //新的字段列表里面不存在旧ID时则为删除了
        Set<String> oldPkSet = oldFields.stream().filter(ModelField::getPrimaryKey).map(ModelField::getName).collect(Collectors.toSet());
        Set<String> newPkSet = newFields.stream().filter(ModelField::getPrimaryKey).map(ModelField::getName).collect(Collectors.toSet());
        Boolean pkChanged = !oldPkSet.equals(newPkSet);
        if (!oldPkSet.isEmpty() && pkChanged) {
            DmpDropPkChange dropPkChange = new DmpDropPkChange(oldPkSet.toArray(oldPkSet.toArray(new String[0])));
            tableChanges.add(dropPkChange);
        }

        List<TableChange> deleteColumnChanges = oldFields.stream().filter(f -> !newIdSet.contains(f.getId())).map(f -> {
            return TableChange.deleteColumn(new String[]{f.getName()}, false);
        }).collect(Collectors.toList());
        List<TableChange> renameColumnChanges = newFields.stream().filter(Objects::nonNull).filter(f -> {
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
        List<TableChange> updateTypeColumnChanges = newFields.stream().filter(Objects::nonNull).filter(f -> {
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

        List<TableChange> updateColumnCommentChanges = newFields.stream().filter(Objects::nonNull).filter(f -> {
            if (oldFieldMap.containsKey(f.getId())) {
                ModelField oldField = oldFieldMap.get(f.getId());
                if (!oldField.getDescription().equals(f.getDescription())) {
                    return true;
                }
            }
            return false;
        }).map(f -> {
            return new DmpUpdateColumnComment(new String[]{f.getName()}, f.getDescription(), getDataType(f), f.getNullable());
        }).collect(Collectors.toList());

        //新字段列表里面，如果ID为null，说明是要新增的
        List<TableChange> addColumnChanges = newFields.stream().filter(f -> f.getId() == null || !oldIdSet.contains(f.getId())).map(f -> {
            return TableChange.addColumn(new String[]{f.getName()}, getDataType(f), f.getNullable(), f.getDescription());
        }).collect(Collectors.toList());
        //顺序应该按:删除，修改，更新类型，新增来，避免冲突

        tableChanges.addAll(deleteColumnChanges);
        tableChanges.addAll(renameColumnChanges);
        tableChanges.addAll(updateTypeColumnChanges);
        tableChanges.addAll(updateColumnCommentChanges);
        tableChanges.addAll(addColumnChanges);

        if (!newPkSet.isEmpty() && pkChanged) {
            DmpAddPkChange addPkChange = new DmpAddPkChange(newPkSet.toArray(newPkSet.toArray(new String[0])));
            tableChanges.add(addPkChange);
        }
        return tableChanges;
    }


    private static DataType getDataType(ModelField field) {
        DataType dataType = null;
        if (field.getType() == ColumnType.GEOMETRY) {
            dataType = new GeometryUDT();
        } else if (field.getType() == ColumnType.DECIMAL) {
            dataType = DataType.fromDDL("decimal(" + field.getPrecision() + "," + field.getScale() + ")");
        } else if (field.getType() == ColumnType.FIXEDSTRING) {
            dataType = DataType.fromDDL(StrUtil.format("char({})", field.getPrecision()));
        } else if (field.getType() == ColumnType.STRING && field.getPrecision() != null && (field.getPrecision() > 0
                || field.getPrecision() < 1000)) {
            dataType = DataType.fromDDL(StrUtil.format("varchar({})", field.getPrecision()));
        } else if (field.getType() == ColumnType.STRING) {
            dataType = DataType.fromDDL("string");
        } else {
            dataType = DataType.fromDDL(field.getType().name());
        }
        return dataType;
    }
}
