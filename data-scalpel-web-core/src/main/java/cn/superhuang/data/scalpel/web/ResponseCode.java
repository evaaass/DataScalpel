package cn.superhuang.data.scalpel.web;

public enum ResponseCode {
    OK("200", "请求成功"),
    UNEXPECTED_INTERNAL_ERROR("500", "意外的内部错误");

    private String code;
    private String desc;

    private ResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
