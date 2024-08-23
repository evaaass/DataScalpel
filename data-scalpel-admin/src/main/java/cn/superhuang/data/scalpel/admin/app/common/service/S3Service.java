package cn.superhuang.data.scalpel.admin.app.common.service;

import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class S3Service {

    @Value("${data-scalpel.s3.bucketName}")
    private String bucketName;

    @Resource
    private MinioClient minioClient;
    @Resource
    private ObjectMapper objectMapper;


    public void persistentTaskInfo(TaskConfiguration taskConfiguration) throws Exception {
        String path = taskConfiguration.getTaskId() + "/" + taskConfiguration.getTaskInstanceId() + "/config.json";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(taskConfiguration));
        PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).stream(inputStream, inputStream.available(), 0).build();
        minioClient.putObject(putObjectArgs);
    }

    //TODO 定期清理minio上的数据
    public void clean() {

    }

}
