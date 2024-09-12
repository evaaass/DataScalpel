package cn.superhuang.test;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.HashMap;
import java.util.Map;

public class SparkSessionTest {
    public static void main(String[] args) {

        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(DbType.POSTGRESQL);
        SysJdbcDialects.initSparkDialects();

        JdbcConfig jdbcConfig = new JdbcConfig();
        jdbcConfig.setDbType(DbType.POSTGRESQL);
        jdbcConfig.setDatabase("test");
        jdbcConfig.setHost("home.superhuang.cn");
        jdbcConfig.setPort(5432);
        jdbcConfig.setUsername("postgres");
        jdbcConfig.setPassword("Gistack@123");
        jdbcConfig.setSchema("public");
        jdbcConfig.setType(DatasourceType.JDBC);

        SparkSession spark01 = SparkSession.builder().master("local")
                .config("spark.sql.codegen.wholeStage", "false")
                .config("spark.sql.crossJoin.enabled", "true")
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.ui.enabled", "false")
                .getOrCreate();


        Map<String, String> options = new HashMap<>();
        options.put("driver", jdbcDialect.getDriver());
        options.put("url", jdbcDialect.buildUrl(jdbcConfig));
        options.put("dbtable", "public.sys_user");
        options.put("user", jdbcConfig.getUsername());
        options.put("password", jdbcConfig.getPassword());

        Dataset<Row> ds01 = spark01.read().format("jdbc").options(options).load();
        ds01.show();
    }
}
