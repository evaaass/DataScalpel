package cn.superhuang.data.scalpel.admin.app.task.service.interceptor;

import cn.superhuang.data.scalpel.app.constant.TaskOptions;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskBaseInfoInterceptor implements TaskSubmitInterceptor {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Override
    public void beforeSubmit(TaskConfiguration taskConfiguration) throws JsonProcessingException {
        if (!(taskConfiguration instanceof SparkTaskConfiguration)) {
            return;
        }
        SparkTaskConfiguration sparkTaskConfiguration = (SparkTaskConfiguration) taskConfiguration;
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setBootstrapServers(kafkaBootstrapServers);

        SparkConfiguration sparkConfiguration = new SparkConfiguration();
        sparkConfiguration.setMaster("local");
        sparkConfiguration.setLogLevel(LogLevel.INFO);
        sparkConfiguration.setConfigs(Maps.newHashMap());

        //TODO 从前端传过来
        sparkTaskConfiguration.getOptions().put(TaskOptions.TASK_CPU, "1");
        sparkTaskConfiguration.getOptions().put(TaskOptions.TASK_MEMORY, "2");
        sparkTaskConfiguration.setKafkaConfig(kafkaConfig);
        sparkTaskConfiguration.setSparkConfiguration(sparkConfiguration);
    }
}
