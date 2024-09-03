package cn.superhuang.data.scalpel.admin.app.common.service;

import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class S3Service {

    @Value("${data-scalpel.s3.bucketName}")
    private String bucketName;

    @Resource
    private MinioClient minioClient;
    @Resource
    private ObjectMapper objectMapper;

    public void putObject(String objectName, InputStream inputStream) throws Exception {
        ObjectWriteResponse res = minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(inputStream, inputStream.available(), 0l).build());
        inputStream.close();
        System.out.println(res);
    }

    public InputStream getObject(String objectName) throws Exception {
        GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        return response;
    }

    public void persistentTaskInfo(TaskConfiguration taskConfiguration) throws Exception {
        String path = "tasks/" + taskConfiguration.getTaskId() + "/" + taskConfiguration.getTaskInstanceId() + "/config.json";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(taskConfiguration));
        PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(path).stream(inputStream, inputStream.available(), 0).build();
        minioClient.putObject(putObjectArgs);
    }

    public void persistentTaskConsoleLog(String taskId, String taskInstanceId, String log) throws Exception {
        String path = "tasks/" + taskId + "/" + taskInstanceId + "/console.log";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(log.getBytes(StandardCharsets.UTF_8));
        PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(path).stream(inputStream, inputStream.available(), 0).build();
        minioClient.putObject(putObjectArgs);
    }

    //TODO 定期清理minio上的数据
    public void clean() {

    }

}
