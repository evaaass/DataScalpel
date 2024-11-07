package cn.superhuang.data.scalpel.model.datamasking;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class DataMaskingRuleCharReplace extends DataMaskingRule {

    private String content;

    private String replaceContent;

    @Override
    public String mask(String data) {
        if (StrUtil.isBlank(data)) {
            return data;
        }
        return data.replaceAll(content,replaceContent);
    }
}
