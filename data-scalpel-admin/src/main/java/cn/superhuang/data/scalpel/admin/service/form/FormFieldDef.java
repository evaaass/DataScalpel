package cn.superhuang.data.scalpel.admin.service.form;


import cn.superhuang.data.scalpel.admin.service.form.enumeration.FormFieldType;
import lombok.Data;

@Data
public class FormFieldDef {
    private FormFieldType type;
    private String displayName;
    private String name;
    private String defaultValue;
    private Boolean required;
    private String tip;
}
