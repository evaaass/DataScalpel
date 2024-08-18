package cn.superhuang.data.scalpel.spark.core.util;

public class SparkUtil {
    public static String quoteIdentifier(String name) {
        return "`" + name + "`";
    }
}
