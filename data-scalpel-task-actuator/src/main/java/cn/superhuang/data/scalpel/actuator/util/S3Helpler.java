package cn.superhuang.data.scalpel.actuator.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class S3Helpler {
    private S3Config s3Config;
    private AmazonS3 amazonS3;
    private Boolean isConnected = false;

    public S3Helpler(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public void connect() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(s3Config.getEndpoint(), "");
        AWSCredentials awsCredentials = new BasicAWSCredentials(s3Config.getAccessId(), s3Config.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        this.amazonS3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration).withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider).disableChunkedEncoding().withPathStyleAccessEnabled(true).build();
        isConnected = false;
    }

    public InputStream getObject(String key) {
        S3Object s3Object = amazonS3.getObject(s3Config.getBucket(), key);
        return s3Object.getObjectContent();
    }

    public void putObject(String key, InputStream inputStream) {
        amazonS3.putObject(s3Config.getBucket(), key, inputStream, new ObjectMetadata());
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
