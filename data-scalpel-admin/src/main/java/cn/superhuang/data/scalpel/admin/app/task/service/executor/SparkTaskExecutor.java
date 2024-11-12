package cn.superhuang.data.scalpel.admin.app.task.service.executor;


import cn.superhuang.data.scalpel.actuator.util.CanvasUtil;
import cn.superhuang.data.scalpel.admin.app.common.service.KafkaService;
import cn.superhuang.data.scalpel.admin.app.dispatcher.model.TaskTriggerResult;
import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.service.event.TaskInstanceTriggerCompleteEvent;
import cn.superhuang.data.scalpel.admin.app.task.service.interceptor.TaskSubmitInterceptor;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.TaskKill;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.definition.BatchCanvasTaskDefinition;
import cn.superhuang.data.scalpel.model.task.definition.SparkTaskDefinition;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class SparkTaskExecutor extends TaskBaseExecutor {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private KafkaService kafkaService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private List<TaskSubmitInterceptor> interceptors;

    @Override
    public Boolean support(TaskType type) {
        return type == TaskType.BATCH_CANVAS || type == TaskType.BATCH_SPARK_SQL || type == TaskType.STREAM_CANVAS || type == TaskType.STREAM_SPARK_SQL;
    }

    @Override
    public void execute(Task task, TaskInstance instance) throws Exception {
        TaskBaseDefinition taskBaseDefinition = objectMapper.readValue(task.getDefinition(), TaskBaseDefinition.class);

        BatchCanvasTaskDefinition definition = (BatchCanvasTaskDefinition) taskBaseDefinition;
        SparkConfiguration sparkConfiguration = new SparkConfiguration();
        sparkConfiguration.setMaster("local");
        sparkConfiguration.setLogLevel(LogLevel.INFO);
        sparkConfiguration.setConfigs(new HashMap<>());

        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setBootstrapServers(bootstrapServers);

        CanvasTaskConfiguration canvasTaskConfiguration = new CanvasTaskConfiguration();
        canvasTaskConfiguration.setCanvas(objectMapper.writeValueAsString(CanvasUtil.fromDwxCanvasContent(definition.getCanvas())));
        canvasTaskConfiguration.setTaskId(task.getId());
        canvasTaskConfiguration.setTaskName(task.getName());
        canvasTaskConfiguration.setTaskInstanceId(instance.getId());
        canvasTaskConfiguration.setSparkConfiguration(sparkConfiguration);
        canvasTaskConfiguration.setType(TaskType.BATCH_CANVAS);
        canvasTaskConfiguration.setKafkaConfig(kafkaConfig);
        canvasTaskConfiguration.setCycleType(null);
        canvasTaskConfiguration.setPlanTriggerTime(null);
        canvasTaskConfiguration.setOptions(Maps.newHashMap());
        canvasTaskConfiguration.setModelMap(Maps.newHashMap());
        canvasTaskConfiguration.setDatasourceMap(Maps.newHashMap());

        for (TaskSubmitInterceptor interceptor : interceptors) {
            interceptor.beforeSubmit(canvasTaskConfiguration);
        }

        kafkaService.sendTaskTrigger(canvasTaskConfiguration);
    }

    //TODO dispatcher消费这个topic，消费后发送日志，kill前后也发个日志
    @Override
    public void kill(String channelId)  {
        kafkaService.sendTaskKill(new TaskKill(channelId));
    }

    @KafkaListener(topics = {"data_scalpel_trigger_result"})
    public void consumeTaskTriggerResult(String taskTriggerResultContent) {
        try {
            TaskTriggerResult result = objectMapper.readValue(taskTriggerResultContent, TaskTriggerResult.class);
            applicationEventPublisher.publishEvent(new TaskInstanceTriggerCompleteEvent(this, result));
        } catch (Exception e) {
            log.error("处理任务结果失败:" + e.getMessage(), e);
        }
    }
}