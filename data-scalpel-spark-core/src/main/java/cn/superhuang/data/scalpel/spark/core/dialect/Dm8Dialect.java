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
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.types.DataTypes.BooleanType;

public class Dm8Dialect extends DsJdbcDialect implements Serializable {
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
    private String getPkKey(String tableName) {
        if (tableName.contains(".")) {
            tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
        }
        return tableName.replaceAll("\"", "") + "_pkey";
    }

    @Override
    public String getAddTablePrimaryKeyQuery(String tableName, List<String> pkList) {
        String pkKey = getPkKey(tableName);
        return StrUtil.format(" alter table {} add constraint \"{}\" NOT CLUSTER primary key({})", tableName, pkKey, String.join(",", pkList));
    }

    @Override
    public String getDropTablePrimaryKeyQuery(String tableName, List<String> pkList) {
        String pkKey = getPkKey(tableName);
        return StrUtil.format("alter table {} drop constraint \"{}\"", tableName, pkKey);
    }

    @Override
    public String getUpdateTableCommentQuery(String tableName, String comment) {
        return StrUtil.format("comment on table {} is '{}'", tableName, comment);
    }

    @Override
    public String getUpdateColumnCommentQuery(String tableName, String column, String type, String comment, Boolean nullable) {
        return StrUtil.format("comment on column {}.{} is '{}'", tableName, column, comment);
    }

    @Override
    public String getUpdateColumnNullableTQuery(String tableName, String column, String type, String comment, Boolean nullable) {
        if (nullable) {
            return StrUtil.format("alter table {} alter column {} set null");
        } else {
            return StrUtil.format("alter table {} alter column {} set not null");
        }

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
