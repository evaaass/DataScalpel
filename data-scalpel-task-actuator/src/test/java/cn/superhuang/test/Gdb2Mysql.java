package cn.superhuang.test;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import com.esri.gdb.FileGDB;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;

import java.util.HashMap;
import java.util.Map;

public class Gdb2Mysql {
    public static void main(String[] args) {
//        String[] tables=FileGDB.listTableNames("/Users/huangchao/Downloads/SLGC_huajie.gdb",new Configuration());
//        for (String table : tables) {
//            System.out.println(table);
//        }

        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(DbType.MYSQL);
        SysJdbcDialects.initSparkDialects();

        JdbcConfig jdbcConfig = new JdbcConfig();
        jdbcConfig.setDbType(DbType.MYSQL);
        jdbcConfig.setDatabase("test");
        jdbcConfig.setHost("10.8.56.83");
        jdbcConfig.setPort(8888);
        jdbcConfig.setUsername("root");
        jdbcConfig.setPassword("Jxstjh@123");
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
        options.put("dbtable", "SLGC_BHFWX");
        options.put("user", jdbcConfig.getUsername());
        options.put("password", jdbcConfig.getPassword());

        Dataset<Row> ds01 = spark01.read().format("com.esri.gdb")
                .option("path", "/Users/huangchao/Downloads/SLGC_huajie.gdb")
                .option("name", "SLGC_GLFWX").
                load().drop("SHAPE");
        ds01.printSchema();
        ds01.where("OBJECTID in (15298,15033)").show();

//        for (String fieldName : ds01.schema().fieldNames()) {
//            StructField field = ds01.schema().apply(fieldName);
//            System.out.println(fieldName + "|" + field.metadata().json());
//        }
//        ds01.write().format("jdbc").mode(SaveMode.Overwrite).options(options).save();
//
//        Dataset<Row> ds02 = spark01.read().format("com.esri.gdb")
//                .option("path", "/Users/huangchao/Downloads/SLGC_huajie.gdb")
//                .option("name", "SLGC_GLFWX").
//                load().drop("SHAPE");
//        ds02.printSchema();
//        for (String fieldName : ds02.schema().fieldNames()) {
//            StructField field = ds01.schema().apply(fieldName);
//            System.out.println(fieldName + "|" + field.metadata().json());
//        }
//        options.put("dbtable", "SLGC_GLFWX");
//        ds02.write().format("jdbc").mode(SaveMode.Overwrite).options(options).save();
    }
}
