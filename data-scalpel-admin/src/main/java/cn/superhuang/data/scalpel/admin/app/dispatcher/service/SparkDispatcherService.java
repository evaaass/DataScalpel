package cn.superhuang.data.scalpel.admin.app.dispatcher.service;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.app.common.service.KafkaService;
import cn.superhuang.data.scalpel.admin.app.common.service.S3Service;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.RunningTaskInfo;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;
import cn.superhuang.data.scalpel.admin.app.dispatcher.service.runner.SparkTaskRunner;
import cn.superhuang.data.scalpel.app.constant.KafkaTopic;
import cn.superhuang.data.scalpel.lib.docker.cli.model.DockerContainerStatus;
import cn.superhuang.data.scalpel.model.task.TaskKill;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


//增加心跳逻辑，防止OOM；或者研究OOM的时候一定让容器退出的机制，应该是有的
@Slf4j
@Service
public class SparkDispatcherService {

    @Resource
    private SparkTaskRunner taskRunner;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private S3Service s3Service;
    @Resource
    private KafkaService kafkaService;
    @Value("${data-scalpel.task.timeout}")
    private Integer defaultTaskTimeout;

    private final Map<String, RunningTaskInfo> runningTaskMap = new HashMap<>();

    @KafkaListener(topics = {"data_scalpel_task_trigger"})
    public void consumeTask(String taskContent) {
        try {
            //TODO 增加一个开关，停止接口
            System.out.println(taskContent);
            SparkTaskConfiguration taskConfiguration = (SparkTaskConfiguration) objectMapper.readValue(taskContent, TaskConfiguration.class);
            s3Service.persistentTaskInfo(taskConfiguration);
            String channelId = taskRunner.run(taskConfiguration);
            kafkaService.sendTriggerResult(TaskTriggerResult.builder().success(true).channelId(channelId).taskId(taskConfiguration.getTaskId()).taskInstanceId(taskConfiguration.getTaskInstanceId()).build());
            runningTaskMap.put(taskConfiguration.getTaskId() + "_" + taskConfiguration.getTaskInstanceId(), new RunningTaskInfo(channelId, taskConfiguration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"data_scalpel_task_dispatcher_result"})
    public void consumeTaskResult(String taskResultContent) {
        try {
            TaskResult taskResult = objectMapper.readValue(taskResultContent, TaskResult.class);
            if (runningTaskMap.containsKey(taskResult.getUniqueKey())) {
                RunningTaskInfo runningTaskInfo = runningTaskMap.get(taskResult.getTaskId() + "_" + taskResult.getTaskInstanceId());
                String channelId = runningTaskInfo.getChannelId();
                String consoleLog = taskRunner.getLog(channelId);
                runningTaskMap.remove(taskResult.getUniqueKey());
                taskRunner.kill(channelId);
                s3Service.persistentTaskConsoleLog(taskResult.getTaskId(), taskResult.getTaskInstanceId(), consoleLog);
            }
            kafkaService.sendTaskResult(taskResult);
            System.out.println(taskResultContent);
        } catch (Exception e) {
            log.error("处理任务结果失败:" + e.getMessage(), e);
        }
    }

    @KafkaListener(topics = {KafkaTopic.TOPIC_TASK_KILL})
    public void consumeTaskKill(String taskKillContent) {
        try {
            TaskKill taskKill = objectMapper.readValue(taskKillContent, TaskKill.class);
            taskRunner.kill(taskKill.getChannelId());
        } catch (Exception e) {
            log.error("处理任务结果失败:" + e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelay = 30 * 1000)
    public void monitorRunningTaskState() {
        for (RunningTaskInfo runningTaskInfo : runningTaskMap.values()) {
            if (runningTaskInfo.isTimeout(defaultTaskTimeout)) {
                log.error("任务" + runningTaskInfo.getTaskConfiguration().getUniqueKey() + "运行超时，强制kill容器");
                killDockerContainer(runningTaskInfo.getChannelId());
                kafkaService.sendTaskResult(TaskResult.errorResult("任务超时", null));
            }
        }
    }

    public DockerContainerStatus getDockerContainerStatus(String channelId) throws JsonProcessingException {
        String containerState = RuntimeUtil.execForStr("docker", "ps", "-a", "--format", "json", "--filter", StrUtil.format("id={}", channelId));
        return objectMapper.readValue(containerState, DockerContainerStatus.class);
    }

    public void killDockerContainer(String channelId) {
        String res = RuntimeUtil.execForStr("docker", "rm", "-f", channelId);
        System.out.println(res);
    }
}