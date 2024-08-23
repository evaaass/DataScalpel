package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.DockerContainerStatus;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.enums.TaskRunnerInstanceState;
import cn.superhuang.data.scalpel.admin.config.DockerRunnerProperties;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DockerRunner implements ITaskRunner {
    @Value("${data-scalpel.s3.bucketName}")
    private String bucket;
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioConfig minioConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DockerRunnerProperties dockerRunnerProperties;

    @Override
    public String run(TaskConfiguration taskConfiguration) throws Exception {
        S3Config s3Config = new S3Config();
        s3Config.setAccessId(minioConfig.getAccessKey());
        s3Config.setSecretKey(minioConfig.getSecretKey());
        s3Config.setEndpoint(minioConfig.getEndpoint());
        s3Config.setBucket(bucket);
        s3Config.setType(DatasourceType.S3);

        List<String> commands = new ArrayList<>();
        commands.add("docker");
        commands.add("run");
        commands.add("-d");
        commands.add("--name");
        commands.add(taskConfiguration.getTaskId() + "_" + taskConfiguration.getTaskInstanceId());
        commands.addAll(dockerRunnerProperties.getCommands());
        commands.add(taskConfiguration.getTaskId());
        commands.add(taskConfiguration.getTaskInstanceId());
//        commands.add(StrUtil.format("'{}'", objectMapper.writeValueAsString(s3Config).replaceAll("\\\"", "\\\\\\\"")));
        commands.add(objectMapper.writeValueAsString(s3Config));


        System.out.println("commands:" + CollectionUtil.join(commands, " "));
        String res = RuntimeUtil.execForStr(commands.toArray(new String[]{})).trim();
        System.out.println(res.length());
        if (res.length() != 64) {
            throw new RuntimeException("调度容器失败" + res);
        }
        return res;
    }


    @Override
    public void kill(String channel) {
        RuntimeUtil.execForStr("docker", "kill", channel);
    }

    @Override
    public TaskRunnerInstanceInfo getInfo(String channelId) throws Exception {
        String containerState = RuntimeUtil.execForStr("docker", "ps", "-a", "--format", "json", "--filter", StrUtil.format("id={}", channelId));
        TaskRunnerInstanceInfo instanceInfo = new TaskRunnerInstanceInfo();
        if (StrUtil.isBlank(containerState)) {
            instanceInfo.setState(TaskRunnerInstanceState.FINISHED);
        } else {
            DockerContainerStatus status = objectMapper.readValue(containerState, DockerContainerStatus.class);
            if (status.getState().equals("exited")) {
                instanceInfo.setState(TaskRunnerInstanceState.FINISHED);
            } else if (status.getState().equals("running")) {
                instanceInfo.setState(TaskRunnerInstanceState.RUNNING);
            } else {
                throw new RuntimeException("未知的容器状态：" + status.getState());
            }
        }
        return instanceInfo;
    }
}