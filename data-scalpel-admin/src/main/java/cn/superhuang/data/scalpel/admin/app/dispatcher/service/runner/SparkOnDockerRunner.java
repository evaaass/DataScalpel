package cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.RunningTaskInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskRunnerInstanceInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.enums.TaskRunnerInstanceState;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceCompleteEvent;
import cn.superhuang.data.scalpel.admin.config.DockerRunnerProperties;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.lib.docker.cli.DockerClient;
import cn.superhuang.data.scalpel.lib.docker.cli.model.DockerContainerStatus;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    private final LinkedBlockingQueue<RunningTaskInfo> queueTasks = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, RunningTaskInfo> runningTaskMap = new ConcurrentHashMap<>();

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
        queueTasks.offer(new RunningTaskInfo(res, taskConfiguration));

        return res;
    }

    @Override
    public void kill(String channelId) {
        dockerClient.killContainer(channelId);
        runningTaskMap.values().stream().filter(task -> task.getChannelId().equals(channelId)).findAny().ifPresent(runningTask -> {
            String key = runningTask.getTaskConfiguration().getTaskId() + "_" + runningTask.getTaskConfiguration().getTaskInstanceId();
            runningTaskMap.remove(key);
            refreshCapacity();
        });
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
        if (!queueTasks.isEmpty()) {
            log.info("队列长度：{}", queueTasks.size());
        }
        RunningTaskInfo runningTask = queueTasks.peek();
        if (runningTask == null) {
            return;
        }
        if (availableCpu > runningTask.getTaskConfiguration().getCpu() && availableMemory > runningTask.getTaskConfiguration().getMemory()) {
            Boolean removeResult = queueTasks.remove(runningTask);
            log.info("队列移除结果：{}", removeResult);
            dockerClient.startContainer(runningTask.getChannelId());
            String key = runningTask.getTaskConfiguration().getTaskId() + "_" + runningTask.getTaskConfiguration().getTaskInstanceId();
            runningTaskMap.put(key, runningTask);
            refreshCapacity();
        }
    }

    @EventListener
    public void handlerTaskInstanceCompletedEvent(TaskInstanceCompleteEvent taskInstanceCompleteEvent) {
        try {
            TaskResult taskResult = taskInstanceCompleteEvent.getTaskResult();
            String key = taskResult.getTaskId() + "_" + taskResult.getTaskInstanceId();
            SparkTaskConfiguration sparkTaskConfiguration = taskInstanceCompleteEvent.getSparkTaskConfiguration();
            //TODO 好好的理一理时间和topic
            if (sparkTaskConfiguration == null) {
                return;
            }
            runningTaskMap.remove(key);
            refreshCapacity();
        } catch (Exception e) {
            log.error("归档日志到S3失败：" + e.getMessage(), e);
        }
    }

    private synchronized void refreshCapacity() {
        Integer usedCpu = 0;
        Integer usedMemory = 0;
        for (RunningTaskInfo runningTask : runningTaskMap.values()) {
            usedCpu = usedCpu + runningTask.getTaskConfiguration().getCpu();
            usedMemory = usedMemory + runningTask.getTaskConfiguration().getMemory();
        }
        availableCpu = capacityMemory - usedCpu;
        availableMemory = capacityMemory - usedMemory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        availableCpu = capacityCpu;
        availableMemory = capacityMemory;
    }
}