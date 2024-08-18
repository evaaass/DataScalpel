package cn.superhuang.data.scalpel.admin.util;

import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DecimalType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

public class SchemaUtil {

    public static List<DataTableColumn> convertToDataTableColumn(StructType structType) {
        List<DataTableColumn> fields = new ArrayList<>();
        for (StructField structField : structType.fields()) {
            DataTableColumn field = new DataTableColumn();
            field.setName(structField.name());
            //TODO 换成中文，考虑从metadata维护
            field.setCnName(structField.name());
            field.setType(getColumnType(structField.dataType(), null));
            if (structField.metadata().contains("precision")) {
                field.setPrecision((int) structField.metadata().getLong("precision"));
            }
            if (structField.metadata().contains("scale")) {
                field.setScale((int) structField.metadata().getLong("scale"));
            }
            fields.add(field);
        }
        return fields;
    }

    public static ColumnType getColumnType(DataType dataType, String originType) {
        if (originType != null && (originType.equalsIgnoreCase("geometry") || originType.equalsIgnoreCase("ST_GEOMETRY"))) {
            return ColumnType.GEOMETRY;
        }
        for (ColumnType value : ColumnType.values()) {
            if (value.getSparkType().equals(dataType.typeName().replaceAll("\\([^\\)]+\\)", ""))) {
                return value;
            }
        }
        return ColumnType.STRING;
    }
}
