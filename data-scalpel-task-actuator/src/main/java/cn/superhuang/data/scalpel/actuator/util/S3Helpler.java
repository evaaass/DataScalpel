package cn.superhuang.data.scalpel.actuator.util;


import cn.hutool.core.io.IoUtil;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider;
import org.apache.hadoop.shaded.org.apache.kerby.kerberos.kerb.client.preauth.pkinit.ClientConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;

public class S3Helpler {
    private S3Config s3Config;
    private S3Client s3Client;
    private Boolean isConnected = false;

    public S3Helpler(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public void connect() {
        s3Client = S3Client.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Config.getAccessId(), s3Config.getSecretKey())))  // 使用 AWS 配置文件中的凭证
                .build();
        isConnected = true;
    }

    public InputStream getObject(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(s3Config.getBucket()).key(key).build();
        return s3Client.getObject(getObjectRequest);
    }

    public void putObject(String key, InputStream inputStream) {
        RequestBody requestBody = RequestBody.fromInputStream(inputStream, 0l);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(key)
                .build();

        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);
        System.out.println(putObjectResponse);
    }

    public TaskConfiguration getTaskConfiguration(String taskId, String taskInstanceId) throws IOException {
        String key = "tasks/" + taskId + "/" + taskInstanceId + "/config.json";
        InputStream inputStream = getObject(key);
        return JsonUtil.objectMapper.readValue(inputStream, TaskConfiguration.class);
    }


    public void saveTaskConfiguration(TaskConfiguration taskConfiguration) throws IOException {
        String key = "tasks/" + taskConfiguration.getTaskId() + "/" + taskConfiguration.getTaskInstanceId() + "/config.json";
        String content = JsonUtil.objectMapper.writeValueAsString(taskConfiguration);
        putObject(key, IoUtil.toUtf8Stream(content));
    }
}
