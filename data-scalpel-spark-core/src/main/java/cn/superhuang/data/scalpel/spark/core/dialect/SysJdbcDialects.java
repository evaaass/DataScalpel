package cn.superhuang.data.scalpel.spark.core.dialect;


import cn.superhuang.data.scalpel.model.enumeration.DbType;
import org.apache.spark.sql.jdbc.*;

import java.util.ArrayList;
import java.util.List;

public class SysJdbcDialects {

    private static List<SysJdbcDialect> dialects = new ArrayList<>();

    static {
        dialects.add(new ClickhouseDialect());
        dialects.add(new Dm8Dialect());
        dialects.add(new GaussDBDwsDialect());
        dialects.add(new HighGoDialect());
        dialects.add(new HiveDialect());
        dialects.add(new KingBase8R3Dialect());
        dialects.add(new KingBase8R6Dialect());
        dialects.add(new MySQLDialect());
        dialects.add(new OceanBaseMySQLDialect());
        dialects.add(new OpenGaussDWSDialect());
        dialects.add(new OracleDialect());
        dialects.add(new OracleSDEDialect());
        dialects.add(new PostgresDialect());
        dialects.add(new PostGISSDEDialect());
        dialects.add(new SQLServerDialect());
        dialects.add(new SQLServerSDEDialect());
        dialects.add(new TDEngineDialect());
        dialects.add(new TDEngineRSDialect());


    }

    public static void initSparkDialects() {
        JdbcDialects.unregisterDialect(PostgresDialect$.MODULE$.apply());
        JdbcDialects.unregisterDialect(MySQLDialect$.MODULE$.apply());
        JdbcDialects.unregisterDialect(OracleDialect$.MODULE$.apply());
        JdbcDialects.unregisterDialect(MsSqlServerDialect$.MODULE$.apply());


        JdbcDialects.registerDialect(new ClickhouseDialect());
        JdbcDialects.registerDialect(new Dm8Dialect());
        JdbcDialects.registerDialect(new GaussDBDwsDialect());
//        JdbcDialects.registerDialect(new HighGoDialect());
        JdbcDialects.registerDialect(new HiveDialect());
        JdbcDialects.registerDialect(new KingBase8R3Dialect());
        JdbcDialects.registerDialect(new KingBase8R6Dialect());
        JdbcDialects.registerDialect(new MySQLDialect());
//        JdbcDialects.registerDialect(new OceanBaseMySQLDialect());
        JdbcDialects.registerDialect(new OpenGaussDWSDialect());
        JdbcDialects.registerDialect(new OracleDialect());
        JdbcDialects.registerDialect(new PostgresDialect());
        JdbcDialects.registerDialect(new SQLServerDialect());
        JdbcDialects.registerDialect(new TDEngineRSDialect());
    }

    public static SysJdbcDialect get(DbType type) {
        for (SysJdbcDialect dialect : dialects) {
            if (dialect.canHandle(type)) {
                return dialect;
            }
        }
        throw new RuntimeException("不支持得数据库类型%s，请联系技术支持".formatted(type));
    }
}