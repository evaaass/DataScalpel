package cn.superhuang.data.scalpel.admin.service.form.field;

import lombok.Data;

@Data
public class InputTextAreaField extends InputField {
    private String pattern;
    private Integer minLength;
    private Integer maxLength;
}
