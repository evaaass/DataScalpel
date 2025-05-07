package cn.superhuang.test;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.execution.QueryExecution;
import org.apache.spark.sql.functions;

import java.util.HashMap;
import java.util.Map;

public class LineageTest {
    public static void main(String[] args) {

        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(DbType.POSTGRESQL);
        DsJdbcDialects.initSparkDialects();

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

        Dataset<Row> newDs = ds01.select(functions.expr("concat(id,'_',user_name)").as("superhuang"));
        newDs = newDs.select(functions.expr("concat(superhuang,'_haha')").as("superhuang"));
        QueryExecution queryExecution=newDs.queryExecution();
        LogicalPlan logicalPlan = queryExecution.logical();
        System.out.println(logicalPlan);
        System.out.println(logicalPlan.prettyJson());
    }
}
