package cn.superhuang.data.scalpel.apiserver.util;


import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import org.ssssssss.magicapi.core.model.BaseDefinition;
import org.ssssssss.magicapi.core.model.DataType;

import java.sql.Date;
import java.util.Iterator;

public class DefinitionUtil {

    public static BaseDefinition buildDefinition(String name, String value, DataType type, String desc, Boolean required) {
        BaseDefinition definition = new BaseDefinition();
        definition.setName(name);
        definition.setValue(value);
        definition.setDescription(desc);
        definition.setRequired(required);
        definition.setDataType(type);
        return definition;
    }


    public static DataType dmpTypeToMagicDataType(ColumnType type) {
        if (type==ColumnType.STRING||type==ColumnType.FIXEDSTRING) {
            return DataType.String;
        } else if (type==ColumnType.INTEGER) {
            return DataType.Integer;
        } else if (type==ColumnType.LONG) {
            return DataType.Long;
        } else if (type==ColumnType.DOUBLE) {
            return DataType.Double;
        } else if (type==ColumnType.BOOLEAN) {
            return DataType.Boolean;
        } else if (type==ColumnType.FLOAT) {
            return DataType.Float;
        } else if (type==ColumnType.BYTE) {
            return DataType.Byte;
        } else if (type==ColumnType.SHORT) {
            return DataType.Short;
        } else if (type==ColumnType.DATE||type==ColumnType.TIMESTAMP) {
            return DataType.Date;
        }else {
            return DataType.Object;
        }
    }

    public static DataType classToMagicDataType(Object obj) {
        if (obj instanceof String) {
            return DataType.String;
        } else if (obj instanceof Integer) {
            return DataType.Integer;
        } else if (obj instanceof Long) {
            return DataType.Long;
        } else if (obj instanceof Double) {
            return DataType.Double;
        } else if (obj instanceof Boolean) {
            return DataType.Boolean;
        } else if (obj instanceof Float) {
            return DataType.Float;
        } else if (obj instanceof Byte) {
            return DataType.Byte;
        } else if (obj instanceof Short) {
            return DataType.Short;
        } else if (obj instanceof Date) {
            return DataType.Date;
        } else {
            return DataType.Object;
        }
    }
}
