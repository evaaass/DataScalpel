package util;


import cn.hutool.core.util.StrUtil;

public class JdbcCommentUtil {

    public static String[] commentSplitChars = new String[]{"\r\n", "\n"};

    public static String getComment(String alias, String description) {
        if (StrUtil.isNotBlank(alias) && StrUtil.isNotBlank(description)) {
            return alias + "\r\n" + description;
        }
        if (StrUtil.isNotBlank(alias) && StrUtil.isBlank(description)) {
            return alias;
        }
        if (StrUtil.isBlank(alias) && StrUtil.isNotBlank(description)) {
            return description;
        }
        return "";
    }

    public static String getAliasFromComment(String comment) {
        if (comment == null) {
            return null;
        }
        for (String commentSplitChar : commentSplitChars) {
            if (comment.contains(commentSplitChar)) {
                return comment.substring(0, comment.indexOf(commentSplitChar));
            }
        }
        return comment;
    }

    public static String getDescriptionFromComment(String comment) {
        if (comment == null) {
            return null;
        }
        for (String commentSplitChar : commentSplitChars) {
            if (comment.contains(commentSplitChar)) {
                return comment.substring(comment.indexOf(commentSplitChar) + commentSplitChar.length());
            }
        }
        return null;
    }
}