package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.enumeration.DbType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class OceanBaseMySQLDialect extends MySQLDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = -5399232003729470672L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.OCEANBASE_MYSQL;
    }

    @Override
    public Map<String, String> getSparkExtraOptions() {
        Map<String, String> options = super.getSparkExtraOptions();
        options.put("isolationLevel", "NONE");
        return options;
    }
}
