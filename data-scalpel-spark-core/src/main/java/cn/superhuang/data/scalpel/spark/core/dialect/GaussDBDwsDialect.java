package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GaussDBDwsDialect extends PostgresDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = 6314900785676196697L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.GAUSS_DB_DWS;
    }

    @Override
    public String getDriver() {
        return "com.huawei.gauss200.jdbc.Driver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        Map<String, String> defaultParams = new HashMap<>();
        defaultParams.put("stringtype", "unspecified");

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        return StrUtil.format("jdbc:gaussdb://{}:{}/{}{}", config.getHost(), config.getPort(), config.getDatabase(), extraParams);
    }

//    @Override
//    public String getPreviewQuery(String table, Long limit, Long offset) {
//        return StrUtil.format("select * from {} limit {},{}", table, limit, offset);
//    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:gaussdb:");
    }
}
