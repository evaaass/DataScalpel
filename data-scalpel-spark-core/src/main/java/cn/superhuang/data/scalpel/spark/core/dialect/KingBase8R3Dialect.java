package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class KingBase8R3Dialect extends PostgresDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = -8234286457496688074L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.KING_BASE_8_R3;
    }

    @Override
    public String getDriver() {
        return "com.kingbase83.Driver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        Map<String, String> defaultParams = new HashMap<>();

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);
        return "jdbc:kingbase83://%s:%d/%s%s".formatted(config.getHost(), config.getPort(), config.getDatabase(), extraParams);
    }

//
//    @Override
//    public String getPreviewQuery(String table, Long limit, Long offset) {
//        return "SELECT * FROM %s limit %d,%d".formatted(table, limit, offset);
//    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:kingbase83:");
    }

    @Override
    public String getSchema(JdbcConfig config) {
        return super.getSchema(config).toUpperCase();
    }
}
