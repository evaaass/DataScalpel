package cn.superhuang.data.scalpel.apiserver.model.enums;


public enum ServiceType {
    STD("STD","标准服务")
    ,SCRIPT("SCRIPT","交互式")
    ,SQL("SQL","向导式");

    private String enName;
    private String cnName;

    private ServiceType(String enName, String cnName) {
        this.enName = enName;
        this.cnName = cnName;
    }

    public static String getCnName(String enName) {
        for (ServiceType value : ServiceType.values()) {
            if (value.getEnName().equals(enName)) {
                return value.getCnName();
            }
        }
        return null;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public static void main(String[] args) {
        System.out.println(ServiceType.STD);
        System.out.println(ServiceType.STD.getCnName());
        System.out.println(ServiceType.getCnName("STD"));
    }

}
