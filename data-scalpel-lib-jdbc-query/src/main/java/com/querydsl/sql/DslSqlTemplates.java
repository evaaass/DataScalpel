package com.querydsl.sql;


import cn.superhuang.data.scalpel.spark.core.dialect.*;

//TODO 以后弄成subQuery和非subQuery两种默认的默认实例，避免每次调用接口都new 对象，减少资源消耗 by SuperHuang
public class DslSqlTemplates {
    public static DsSQLTemplates getSQLTemplates(DsJdbcDialect dialect) {
        if (dialect instanceof PostgresDialect) {
            return new DsPostgreSQLTemplates(true);
        } else if (dialect instanceof OracleDialect) {
            // 设置 printSchema = true
            return new DsOracleTemplates(true);
        } else if (dialect instanceof MySQLDialect) {
            return new DsMySQLTemplates(true);
        } else if (dialect instanceof Dm8Dialect) {
            return new DsDaMengTemplates(true);
        } else if (dialect instanceof TDEngineRSDialect) {
            return new TDEngineRSTemplates(true);
        } else if (dialect instanceof SQLServerDialect) {
            return new DsSQLServer2008Templates(true);
        } else {
            return new DsSQLTemplates("\"");
        }
    }
}
