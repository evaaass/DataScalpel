package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HighGoDialect extends PostgresDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = -4758449047543430979L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.HIGHGO;
    }

    @Override
    public String getDriver() {
        return "com.highgo.jdbc.Driver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        Map<String, String> defaultParams = new HashMap<>();
        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        return "jdbc:highgo://%s:%d/%s%s".formatted(config.getHost(), config.getPort(), config.getDatabase(),extraParams);
    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:highgo:");
    }
}
