package cn.superhuang.data.scalpel.admin.service.form.field;

import cn.superhuang.data.scalpel.admin.service.form.FieldOption;
import lombok.Data;

import java.util.Set;

@Data
public class CheckboxField {
    private Set<FieldOption> options;
}
