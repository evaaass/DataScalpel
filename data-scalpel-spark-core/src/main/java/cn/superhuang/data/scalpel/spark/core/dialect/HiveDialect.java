package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class HiveDialect extends DsJdbcDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = -4758449047543430979L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.HIVE;
    }

    @Override
    public String getDriver() {
        return "org.apache.hive.jdbc.HiveDriver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        java.util.Map<String, String> defaultParams = new HashMap<>();

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        String db = "";
        if (StrUtil.isNotBlank(config.getDatabase())) {
            db = "/" + config.getDatabase();
        }
        return StrUtil.format("jdbc:hive2://{}:{}{}{}", config.getHost(), config.getPort(), db, extraParams);
    }

    @Override
    public String getPreviewQuery(String table, Long limit, Long offset) {
        if (offset != 0) {
            throw new RuntimeException("暂未适配HIVE得分页查询，请联系厂商");
        }
        return StrUtil.format("select * from {} limit {}", table, limit);
    }

    public String getDropTableQuery(String table, JdbcConfig config) {
        return String.format("drop table if exists %s", table);
    }

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:hive2:");
    }
}
