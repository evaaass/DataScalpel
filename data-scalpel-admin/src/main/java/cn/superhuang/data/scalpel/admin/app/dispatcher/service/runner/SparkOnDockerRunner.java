package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.RunningTaskInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.enums.TaskRunnerInstanceState;
import cn.superhuang.data.scalpel.admin.config.DockerRunnerProperties;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.lib.docker.cli.DockerClient;
import cn.superhuang.data.scalpel.lib.docker.cli.model.DockerContainerStatus;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class SparkOnDockerRunner implements SparkTaskRunner, InitializingBean {
    @Value("${data-scalpel.s3.bucketName}")
    private String bucket;
    @Resource
    private MinioConfig minioConfig;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DockerRunnerProperties dockerRunnerProperties;
    @Value("${data-scalpel.dispatcher.runner.docker.capacity-cpu}")
    private Integer capacityCpu;
    @Value("${data-scalpel.dispatcher.runner.docker.capacity-memory}")
    private Integer capacityMemory;

    private Integer availableCpu;
    private Integer availableMemory;

    private final LinkedBlockingQueue<RunningTaskInfo> queue = new LinkedBlockingQueue<>();

    private DockerClient dockerClient = new DockerClient();

    @Override
    public String run(SparkTaskConfiguration taskConfiguration) throws Exception {
        S3Config s3Config = new S3Config();
        s3Config.setAccessId(minioConfig.getAccessKey());
        s3Config.setSecretKey(minioConfig.getSecretKey());
        s3Config.setEndpoint(minioConfig.getEndpoint());
        s3Config.setBucket(bucket);

        List<String> commands = new ArrayList<>();
        commands.add("docker");
        commands.add("create");
        commands.add("--name");
        commands.add(taskConfiguration.getTaskId() + "_" + taskConfiguration.getTaskInstanceId());
        commands.addAll(dockerRunnerProperties.getCommands());
        commands.add(taskConfiguration.getTaskId());
        commands.add(taskConfiguration.getTaskInstanceId());
        commands.add(objectMapper.writeValueAsString(s3Config));
        System.out.println("commands:" + String.join(" ", commands));
        String res = dockerClient.executeCommand(commands.toArray(new String[]{}));
        if (res.length() != 64) {
            throw new RuntimeException("调度容器失败" + res);
        }
        queue.offer(new RunningTaskInfo(res, taskConfiguration));

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


    @Scheduled(fixedDelay = 1000)
    public void checkQueue() {
        if (!queue.isEmpty()) {
            log.info("队列长度：{}", queue.size());
        }
        RunningTaskInfo queueItem = queue.peek();
        if (queueItem == null) {
            return;
        }
        if (availableCpu > queueItem.getTaskConfiguration().getCpu() && availableMemory > queueItem.getTaskConfiguration().getMemory()) {
            Boolean removeResult = queue.remove(queueItem);
            log.info("队列移除结果：{}", removeResult);
            dockerClient.startContainer(queueItem.getChannelId());
            changeCapacity(-queueItem.getTaskConfiguration().getCpu(), -queueItem.getTaskConfiguration().getMemory());
        }
    }

    private synchronized void changeCapacity(Integer cpu, Integer memory) {
        availableCpu = availableCpu + cpu;
        availableMemory = availableMemory + memory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        availableCpu = capacityCpu;
        availableMemory = capacityMemory;
    }
}