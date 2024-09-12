package cn.superhuang.data.scalpel.admin.app.task.service;

import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import jakarta.annotation.Resource;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class SparkService implements InitializingBean {
    @Resource
    private MinioConfig minioConfig;

    private SparkSession sparkSession;
    private JavaSparkContext sparkContext;

    public SparkSession getSparkSession() {
        return sparkSession;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.sparkSession = SparkSession.builder()
                .config("spark.master", "local[*]")
                .config("spark.sql.codegen.wholeStage", "false")
                .config("spark.sql.crossJoin.enabled", "true")
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.ui.enabled", "false")
                .config("spark.hadoop.fs.s3a.access.key", minioConfig.getAccessKey())
                .config("spark.hadoop.fs.s3a.secret.key", minioConfig.getSecretKey())
                .config("spark.hadoop.fs.s3a.endpoint", minioConfig.getEndpoint())  // 根据实际情况配置，如使用S3标准区域
                .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
                .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .config("spark.hadoop.fs.s3a.path.style.access", "true")
                .config("spark.hadoop.fs.s3a.max.retries", "2")
                .config("spark.hadoop.fs.s3a.retry.limit", "2")
                .config("spark.hadoop.fs.s3a.connection.timeout", "10000")
                .config("spark.hadoop.fs.s3a.ssl.enabled", "false")
                .config("spark.hadoop.fs.s3a.threads.keepalivetime", "60000")
                .config("spark.hadoop.fs.s3a.connection.establish.timeout", "30000")
                .getOrCreate();

        this.sparkContext = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
    }
}
