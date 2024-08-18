package cn.superhuang.data.scalpel.model.enumeration;

public enum DataSaveMode {
    APPEND("Append"), OVERWRITE("Overwrite");

    private String sparkValue;

    public String getSparkValue() {
        return sparkValue;
    }

    DataSaveMode(String sparkValue) {
        this.sparkValue = sparkValue;
    }
}
