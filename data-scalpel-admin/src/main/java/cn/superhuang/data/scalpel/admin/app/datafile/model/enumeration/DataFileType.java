package cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration;


public enum DataFileType {
    CSV("csv"),
    TXT("txt"),
    XLS("xls"),
    XLSX("xlsx"),
    JSON("json"),
    SHP("shp"),
    GDB("gdb");

    private String extName;
    DataFileType(String extName) {
        this.extName = extName;
    }
    public String getExtName() {
        return extName;
    }
}
