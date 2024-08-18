package cn.superhuang.data.scalpel.model.enumeration;

public enum ColumnType {
    BINARY("binary", "binary"),
    BYTE("byte", "byte"),
    SHORT("short", "short"),
    INTEGER("integer", "integer"),
    LONG("long", "long"),
    FLOAT("float", "float"),
    DOUBLE("double", "double"),
    DECIMAL("decimal", "decimal"),
    STRING("string", "string"),
    FIXEDSTRING("fixedstring", "string"),
    BOOLEAN("boolean", "boolean"),
    DATE("date", "date"),
    TIMESTAMP("timestamp", "timestamp"),
    GEOMETRY("geometry", "string");
    private String name;
    private String sparkType;

    public String getName() {
        return name;
    }

    public String getSparkType() {
        return sparkType;
    }


    ColumnType(String name, String sparkType) {
        this.name = name;
        this.sparkType = sparkType;
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
