package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OpenGaussDWSDialect extends PostgresDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = -8435459336944233632L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.OPENGAUSS;
    }

    @Override
    public String getDriver() {
        return "org.opengauss.Driver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        Map<String, String> defaultParams = new HashMap<>();

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        return StrUtil.format("jdbc:opengauss://{}:{}/{}{}", config.getHost(), config.getPort(), config.getDatabase(), extraParams);
    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:opengauss:");
    }
}
