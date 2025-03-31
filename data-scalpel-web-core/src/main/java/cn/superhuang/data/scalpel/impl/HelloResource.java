package cn.superhuang.data.scalpel.impl;

import cn.superhuang.data.scalpel.app.task.model.TimeRangeStrategy;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class HelloResource {

    @GetMapping("/hello")
    public String getAllTasks() {
        return "Hello World!";
    }


    @GetMapping("/date01")
    public Date getDate() {
        return new Date();
    }

    @PostMapping("/date01")
    public String getDate(@RequestBody TimeRangeStrategy strategy) {
        return strategy.toString();
    }

    @GetMapping("/spark-session-test")
    public String sparkSessionTest() throws AnalysisException {

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
                .appName("SpringBootSparkApp_" + UUID.randomUUID().toString()) // 每次使用唯一的 appName
                .config("spark.sql.codegen.wholeStage", "false")
                .config("spark.sql.crossJoin.enabled", "true")
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        try {
            Map<String, String> options = new HashMap<>();
            options.put("driver", jdbcDialect.getDriver());
            options.put("url", jdbcDialect.buildUrl(jdbcConfig));
            options.put("dbtable", "public.sys_user");
            options.put("user", jdbcConfig.getUsername());
            options.put("password", jdbcConfig.getPassword());

            spark01.catalog().listTables().show();

            Dataset<Row> ds01 = spark01.read().format("jdbc").options(options).load();
            ds01.show();
            ds01.createTempView("hahaha");
            spark01.catalog().listTables().show();

        } finally {
            spark01.stop();
        }

        return "Hello World!";
    }
}
