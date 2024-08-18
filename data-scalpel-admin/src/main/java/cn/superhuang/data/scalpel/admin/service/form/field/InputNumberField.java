package cn.superhuang.data.scalpel.admin.service.form.field;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InputNumberField extends InputField {
    private String pattern;
    private Integer minLength;
    private Integer maxLength;
    private Integer precision;
    private BigDecimal step;
}
