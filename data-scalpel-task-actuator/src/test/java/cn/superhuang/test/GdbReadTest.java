package cn.superhuang.test;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import com.esri.gdb.FileGDB;
import com.esri.gdb.GDBOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.sedona.core.serde.SedonaKryoRegistrator;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.util.HashMap;
import java.util.Map;

public class GdbReadTest {
    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        SparkSession spark = SparkSession.builder()
                .config("spark.master", "local")
                .config("spark.sql.codegen.wholeStage", "false")
                .config("spark.sql.crossJoin.enabled", "true")
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.ui.enabled", "true")
                .config("spark.serializer", KryoSerializer.class.getName()) // org.apache.spark.serializer.KryoSerializer
                .config("spark.kryo.registrator", SedonaKryoRegistrator.class.getName())
                .config("spark.sql.datetime.java8API.enabled", "true")
                .getOrCreate();

        String[] tables = FileGDB.listTableNames("/Volumes/HIKSEMI/SuperHuang/数据/GDB/云南滴/空间叠加测试/空间叠加测试数据.gdb", new Configuration());
        for (String table : tables) {
            System.out.println(table);
        }
        //YJJBNT  2662856
        //GDBHMB  4344269
        //BNRGDBHMB 842875
        //GD       5237682
        Dataset<Row> ds = spark.read().format("com.esri.gdb")
                .option("path", "/Volumes/HIKSEMI/SuperHuang/数据/GDB/云南滴/空间叠加测试/空间叠加测试数据.gdb")
                .option("name", "GD")
                .option(GDBOptions.NUM_PARTITIONS(), "4").
                load();

        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(DbType.POSTGRESQL);
        JdbcConfig jdbcConfig = new JdbcConfig();
        jdbcConfig.setDbType(DbType.POSTGRESQL);
        jdbcConfig.setDatabase("test");
        jdbcConfig.setHost("172.20.66.240");
        jdbcConfig.setPort(22227);
        jdbcConfig.setUsername("postgres");
        jdbcConfig.setPassword("Jxstjh@123");
        jdbcConfig.setSchema("public");
        jdbcConfig.setType(DatasourceType.JDBC);
        Map<String, String> options = new HashMap<>();
        options.put("driver", jdbcDialect.getDriver());
        options.put("url", jdbcDialect.buildUrl(jdbcConfig));
        options.put("dbtable", "test_gd_01");
        options.put("user", jdbcConfig.getUsername());
        options.put("password", jdbcConfig.getPassword());
        ds=ds.drop("Shape");
        ds.write().format("jdbc").mode(SaveMode.Overwrite).options(options).save();

        Long endTime = System.currentTimeMillis();
        Long costTime = endTime - startTime;
        System.out.println("耗费时间:" + costTime / 1000 + "秒");
    }
}
