package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.hutool.core.collection.CollectionUtil;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.enums.TaskRunnerInstanceState;
import cn.superhuang.data.scalpel.admin.config.DockerRunnerProperties;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.lib.docker.cli.DockerClient;
import cn.superhuang.data.scalpel.lib.docker.cli.model.DockerContainerStatus;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DockerRunner implements ITaskRunner {
    @Value("${data-scalpel.s3.bucketName}")
    private String bucket;
    @Resource
    private MinioConfig minioConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DockerRunnerProperties dockerRunnerProperties;

    private DockerClient dockerClient = new DockerClient();

    @Override
    public String run(TaskConfiguration taskConfiguration) throws Exception {
        S3Config s3Config = new S3Config();
        s3Config.setAccessId(minioConfig.getAccessKey());
        s3Config.setSecretKey(minioConfig.getSecretKey());
        s3Config.setEndpoint(minioConfig.getEndpoint());
        s3Config.setBucket(bucket);

        List<String> commands = new ArrayList<>();
        commands.add("docker");
        commands.add("run");
        commands.add("-d");
        commands.add("--name");
        commands.add(taskConfiguration.getTaskId() + "_" + taskConfiguration.getTaskInstanceId());
        commands.addAll(dockerRunnerProperties.getCommands());
        commands.add(taskConfiguration.getTaskId());
        commands.add(taskConfiguration.getTaskInstanceId());
        commands.add(objectMapper.writeValueAsString(s3Config));
        System.out.println("commands:" + String.join(" ",commands));
        String res = dockerClient.executeCommand(commands.toArray(new String[]{}));
        if (res.length() != 64) {
            throw new RuntimeException("调度容器失败" + res);
        }
        return res;
    }

    @Override
    public void kill(String channel) {
        dockerClient.killContainer(channel);
    }

    @Override
    public TaskRunnerInstanceInfo getInfo(String channelId) throws Exception {
        DockerContainerStatus status = dockerClient.getContainerStatus(channelId);
        TaskRunnerInstanceInfo instanceInfo = new TaskRunnerInstanceInfo();
        if (status.getState() == null || status.getState().equals("exited")) {
            instanceInfo.setState(TaskRunnerInstanceState.FINISHED);
        } else if (status.getState().equals("running")) {
            instanceInfo.setState(TaskRunnerInstanceState.RUNNING);
        } else {
            instanceInfo.setState(TaskRunnerInstanceState.FAILED);
            log.error("未知的容器状态：" + status.getState());
        }
        return instanceInfo;
    }

    @Override
    public String getLog(String channelId) throws Exception {
        return dockerClient.getContainerLog(channelId);
    }
}