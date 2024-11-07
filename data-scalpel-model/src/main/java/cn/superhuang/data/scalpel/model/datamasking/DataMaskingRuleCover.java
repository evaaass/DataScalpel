package cn.superhuang.data.scalpel.model.datamasking;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class DataMaskingRuleCover extends DataMaskingRule {

    private Integer startIndex;

    private Integer endIndex;

    private char replaceChar;

    //null则为真实长度，根据endIndex-startIndex，然后实际长度来
    private Integer replaceLength;

    @Override
    public String mask(String data) {
        if (StrUtil.isBlank(data)) {
            return data;
        }
        int realStartIndex = startIndex > data.length() ? data.length() : startIndex;
        int realEndIndex = endIndex > data.length() ? data.length() : endIndex;
        Integer realReplaceLength = replaceLength == null ? realEndIndex - realStartIndex : replaceLength;
        String replaceContent = String.format("%0" + 5 + "d", 0).replace('0', replaceChar);

        StringBuilder sb = new StringBuilder(data);
        sb.replace(realStartIndex, realEndIndex, replaceContent);
        return sb.toString();
    }

}
