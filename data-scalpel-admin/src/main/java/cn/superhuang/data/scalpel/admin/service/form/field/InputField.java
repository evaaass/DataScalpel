package cn.superhuang.data.scalpel.admin.service.form.field;

import lombok.Data;

@Data
public class InputField {
    private String pattern;
    private Integer minLength;
    private Integer maxLength;
}