package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.TimestampType$;
import scala.Option;
import scala.Option$;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import static org.apache.spark.sql.types.DataTypes.BooleanType;

public class Dm8Dialect extends SysJdbcDialect implements Serializable {
    @Serial
    private static final long serialVersionUID = 4824719886455270557L;

    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:dm:");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Option getCatalystType(int sqlType, String typeName, int size, MetadataBuilder md) {
        if (typeName.equals("TIMESTAMP")) {
            return Option$.MODULE$.apply(TimestampType$.MODULE$);
        } else {
            return super.getCatalystType(sqlType, typeName, size, md);
        }
    }

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.DAMENG;
    }

    @Override
    public String getDriver() {
        return "dm.jdbc.driver.DmDriver";
    }

    @Override
    public String buildUrl(JdbcConfig config) {
        Map<String, String> defaultParams = new HashMap<>();
//        defaultParams.put("schema", config.getDatabase());

        String extraParams = config.formatParams(defaultParams, "=", "&");
        extraParams = StrUtil.isBlank(extraParams) ? "" : ("?" + extraParams);

        return StrUtil.format("jdbc:dm://{}:{}{}", config.getHost(), config.getPort(), extraParams);
    }

    @Override
    public String getPreviewQuery(String table, Long limit, Long offset) {
        return StrUtil.format("select * from {} limit {},{}", table, offset,limit);
    }

    @Override
    public String getTableSizeQuery(String table, JdbcConfig config) {
        return StrUtil.format("select TABLE_USED_SPACE('{}','{}') * page() from dual", config.getDatabase(), table);
    }

    @Override
    public String getSchema(JdbcConfig config) {
        return config.getDatabase();
    }

    @Override
    public Option<org.apache.spark.sql.jdbc.JdbcType> getJDBCType(DataType dt) {
        if (dt == BooleanType) {
            return Option.apply(new org.apache.spark.sql.jdbc.JdbcType("BIT", Types.BIT));
        } else {
            return super.getJDBCType(dt);
        }
    }
}
