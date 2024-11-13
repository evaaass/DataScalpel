package cn.superhuang.data.scalpel.actuator;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.superhuang.data.scalpel.actuator.util.JsonUtil;
import cn.superhuang.data.scalpel.actuator.util.KafkaHelper;
import cn.superhuang.data.scalpel.actuator.util.S3Helpler;
import cn.superhuang.data.scalpel.model.datasource.config.S3Config;
import cn.superhuang.data.scalpel.model.task.configuration.SparkTaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import cn.superhuang.data.scalpel.actuator.canvas.executor.CanvasExecutor;
import cn.superhuang.data.scalpel.app.task.model.TaskResultSummary;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;

import java.util.Date;

public class ActuatorApplication {
    //taskId taskInstanceId {\"type\":\"S3\",\"params\":{\"accessId\":\"minioadmin\",\"bucket\":\"data-scalpel\",\"endpoint\":\"http://home.superhuang.cn:9000\",\"secretKey\":\"Gistack@123\"}} true
    public static void main(String[] args) throws JsonProcessingException {
        String taskId = args[0];
        String taskInstanceId = args[1];
        String s3ConfigStr = args[2].replace("'", "");
        Boolean debug = false;
        if (args.length > 3) {
            debug = Boolean.parseBoolean(args[3]);
        }

        System.out.println(s3ConfigStr);
        KafkaHelper kafkaHelper = null;
        TaskResult taskResult = new TaskResult();
        taskResult.setTaskId(taskId);
        taskResult.setTaskInstanceId(taskInstanceId);
        taskResult.setStartTime(new Date());
        try {
            S3Helpler s3Helpler = new S3Helpler(JsonUtil.objectMapper.readValue(s3ConfigStr, S3Config.class));
            s3Helpler.connect();

            SparkTaskConfiguration taskConfiguration = (SparkTaskConfiguration) s3Helpler.getTaskConfiguration(taskId, taskInstanceId);
            taskConfiguration.setDebug(debug);
            if (debug) {
                taskConfiguration.getSparkConfiguration().setMaster("local");
                kafkaHelper = new KafkaHelper();
            } else {
                kafkaHelper = new KafkaHelper(taskConfiguration.getKafkaConfig().getBootstrapServers());
            }
            ActuatorContext actuatorContext = ActuatorContext.getOrCreate(taskConfiguration);
            actuatorContext.setKafkaHelper(kafkaHelper);

            actuatorContext.log("我要开始了");

            CanvasExecutor canvasExecutor = new CanvasExecutor(actuatorContext);
            TaskResultSummary taskResultSummary = canvasExecutor.execute();
            taskResult.setSummary(taskResultSummary);
            actuatorContext.log("我要又结束了");

            taskResult.setSuccess(true);
            actuatorContext.destory();
        } catch (Exception e) {
            e.printStackTrace();
            taskResult.setSuccess(false);
            taskResult.setMessage(ExceptionUtil.getSimpleMessage(e));
            taskResult.setDetail(ExceptionUtil.stacktraceToString(e, -1));
        }


        taskResult.setEndTime(new Date());
        if (kafkaHelper != null) {
            kafkaHelper.sendResult(taskResult);
        } else {
            System.out.println(JsonUtil.objectMapper.writeValueAsString(taskResult));
        }
    }
}
