package cn.superhuang.data.scalpel.admin.util;

import cn.hutool.core.util.StrUtil;

public class StringUtil {
    public static final String getNameFromRemark(String remark) {
        String name = remark;
        if (StrUtil.isNotBlank(name)) {
            if (name.contains("\r\n")) {
                name = name.substring(0, name.indexOf("\r\n"));
            } else if (remark.contains(":")) {
                name = name.substring(0, name.indexOf(":"));
            } else if (remark.contains("：")) {
                name = name.substring(0, name.indexOf("："));
            } else if (remark.contains(",")) {
                name = name.substring(0, name.indexOf(","));
            } else if (remark.contains("，")) {
                name = name.substring(0, name.indexOf("，"));
            }
        }
        return name;
    }
}
