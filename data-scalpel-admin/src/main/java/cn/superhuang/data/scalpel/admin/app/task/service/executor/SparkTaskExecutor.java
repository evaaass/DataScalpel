package cn.superhuang.data.scalpel.admin.app.task.service.executor;


import cn.superhuang.data.scalpel.admin.app.common.service.KafkaService;
import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.app.task.service.interceptor.TaskSubmitInterceptor;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.definition.BatchCanvasTaskDefinition;
import cn.superhuang.data.scalpel.model.task.definition.SparkTaskDefinition;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Component
public class SparkTaskExecutor extends TaskBaseExecutor {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private KafkaService kafkaService;

    @Resource
    private List<TaskSubmitInterceptor> interceptors;

    @Override
    public Boolean support(TaskType type) {
        return type==TaskType.BATCH_CANVAS;
    }

    @Override
    public void execute(Task task, TaskInstance instance) throws Exception {
        TaskBaseDefinition taskBaseDefinition = objectMapper.readValue(task.getDefinition(), TaskBaseDefinition.class);

        BatchCanvasTaskDefinition definition= (BatchCanvasTaskDefinition) taskBaseDefinition;
        SparkConfiguration sparkConfiguration = new SparkConfiguration();
        sparkConfiguration.setMaster("local");
        sparkConfiguration.setLogLevel(LogLevel.INFO);
        sparkConfiguration.setConfigs(new HashMap<>());

        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setBootstrapServers(bootstrapServers);

        CanvasTaskConfiguration canvasTaskConfiguration = new CanvasTaskConfiguration();
        canvasTaskConfiguration.setCanvas(definition.getCanvas());
        canvasTaskConfiguration.setTaskId(task.getId());
        canvasTaskConfiguration.setTaskName(task.getName());
        canvasTaskConfiguration.setTaskInstanceId(instance.getId());
        canvasTaskConfiguration.setSparkConfiguration(sparkConfiguration);
        canvasTaskConfiguration.setType(TaskType.BATCH_CANVAS);
        canvasTaskConfiguration.setKafkaConfig(kafkaConfig);
        canvasTaskConfiguration.setCycleType(null);
        canvasTaskConfiguration.setPlanTriggerTime(null);
        canvasTaskConfiguration.setParams(Maps.newHashMap());
        canvasTaskConfiguration.setModelMap(Maps.newHashMap());
        canvasTaskConfiguration.setDatasourceMap(Maps.newHashMap());

        for (TaskSubmitInterceptor interceptor : interceptors) {
            interceptor.beforeSubmit(canvasTaskConfiguration);
        }

        kafkaService.sendTaskTrigger(canvasTaskConfiguration);
    }
}
