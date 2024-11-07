package cn.superhuang.data.scalpel.model.datamasking;

import lombok.Data;

@Data
public class DataMaskingRuleRegularExpression extends DataMaskingRule {

    private String regex;

    private String replaceContent;

    @Override
    public String mask(String data) {
        return "";
    }
}
