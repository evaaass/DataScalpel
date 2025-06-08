package cn.superhuang.test;

import com.esri.gdb.FileGDB;
import com.esri.gdb.GDBOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.sedona.core.serde.SedonaKryoRegistrator;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

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

        String[] tables = FileGDB.listTableNames("/Volumes/HIKSEMI/SuperHuang/数据/云南滴/空间叠加测试/空间叠加测试数据.gdb", new Configuration());
        for (String table : tables) {
            System.out.println(table);
        }
        //YJJBNT  2662856
        //GDBHMB  4344269
        //BNRGDBHMB 842875
        //GD       5237682
        Dataset<Row> ds = spark.read().format("com.esri.gdb")
                .option("path", "/Volumes/HIKSEMI/SuperHuang/数据/云南滴/空间叠加测试/空间叠加测试数据.gdb")
                .option("name", "GD")
                .option(GDBOptions.NUM_PARTITIONS(), "4").
                load();
        ds.printSchema();
        ds.show();
        Long endTime = System.currentTimeMillis();
        Long costTime = endTime - startTime;
        System.out.println("耗费时间:" + costTime / 1000 + "秒");
    }
}
