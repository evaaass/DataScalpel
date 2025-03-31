package cn.superhuang.test;


import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.util.S3Helpler;
import cn.superhuang.data.scalpel.model.datasource.config.KafkaConfig;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class CreateSimpleTaskConfig {
    public static void main(String[] args) throws IOException {
        S3Config s3Config = new S3Config();
        s3Config.setAccessId("minioadmin");
        s3Config.setSecretKey("Gistack@123");
        s3Config.setEndpoint("http://home.superhuang.cn:9000");
        s3Config.setBucket("data-scalpel");

        S3Helpler helpler=new S3Helpler(s3Config);


        SparkConfiguration sparkConfiguration=new SparkConfiguration();
        sparkConfiguration.setMaster("local");
        sparkConfiguration.setLogLevel(LogLevel.INFO);
        sparkConfiguration.setConfigs(new HashMap<>());

        KafkaConfig kafkaConfig=new KafkaConfig();
        kafkaConfig.setBootstrapServers("home.superhuang.cn:9094");


        ObjectMapper objectMapper=new ObjectMapper();
        Canvas canvas=new Canvas();
        CanvasTaskConfiguration taskConfiguration=new CanvasTaskConfiguration();
        taskConfiguration.setCanvas(objectMapper.writeValueAsString(canvas));
        taskConfiguration.setDebug(true);
        taskConfiguration.setTaskId("taskId");
        taskConfiguration.setTaskName("taskName");
        taskConfiguration.setTaskInstanceId("taskInstanceId");
        taskConfiguration.setSparkConfiguration(sparkConfiguration);
//        taskConfiguration.setType(TaskType.BATCH_CANVAS);
        taskConfiguration.setKafkaConfig(kafkaConfig);
        taskConfiguration.setOptions(new HashMap<>());
        taskConfiguration.setModelMap(new HashMap<>());
        taskConfiguration.setDatasourceMap(new HashMap<>());

        helpler.connect();
        helpler.saveTaskConfiguration(taskConfiguration);


    }
}
