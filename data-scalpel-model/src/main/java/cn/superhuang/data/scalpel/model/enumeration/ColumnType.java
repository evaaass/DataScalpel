package cn.superhuang.data.scalpel.model.enumeration;

import java.math.BigDecimal;
import java.util.Date;

public enum ColumnType {
    BINARY("binary", "binary", ColumnTypeCategory.SIMPLE, null,false,false),
    BYTE("byte", "byte", ColumnTypeCategory.SIMPLE, Byte.class,false,false),
    SHORT("short", "short", ColumnTypeCategory.NUMBER, Short.class,false,false),
    INTEGER("integer", "integer", ColumnTypeCategory.NUMBER, Integer.class,false,false),
    LONG("long", "long", ColumnTypeCategory.NUMBER, Long.class,false,false),
    FLOAT("float", "float", ColumnTypeCategory.NUMBER, Float.class,false,false),
    DOUBLE("double", "double", ColumnTypeCategory.NUMBER, Double.class,false,false),
    DECIMAL("decimal", "decimal", ColumnTypeCategory.NUMBER, BigDecimal.class,true,true),
    STRING("string", "string", ColumnTypeCategory.STRING, String.class,true,false),
    FIXEDSTRING("fixedstring", "string", ColumnTypeCategory.STRING, String.class,true,false),
    BOOLEAN("boolean", "boolean", ColumnTypeCategory.BOOLEAN, Boolean.class,false,false),
    DATE("date", "date", ColumnTypeCategory.DATE, Date.class,false,false),
    TIMESTAMP("timestamp", "timestamp", ColumnTypeCategory.DATETIME, Date.class,false,false),
    GEOMETRY("geometry", "string", ColumnTypeCategory.SIMPLE, null,false,false);
    private String name;
    private String sparkType;
    private ColumnTypeCategory category;
    private Class javaClassType;
    private Boolean supportPrecision;
    private Boolean supportScale;

    public String getName() {
        return name;
    }

    public String getSparkType() {
        return sparkType;
    }

    public ColumnTypeCategory getCategory() {
        return category;
    }

    public Class getJavaClassType() {
        return javaClassType;
    }

    public Boolean getSupportScale() {
        return supportScale;
    }

    public Boolean getSupportPrecision() {
        return supportPrecision;
    }

    ColumnType(String name, String sparkType, ColumnTypeCategory category, Class javaClassType, Boolean supportPrecision, Boolean supportScale) {
        this.name = name;
        this.sparkType = sparkType;
        this.category = category;
        this.javaClassType = javaClassType;
        this.supportPrecision = supportPrecision;
        this.supportScale = supportScale;
    }

    public static ColumnType fromName(String name) {
        for (ColumnType value : values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
