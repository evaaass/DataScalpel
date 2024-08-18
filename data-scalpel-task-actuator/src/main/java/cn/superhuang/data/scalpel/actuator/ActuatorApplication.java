package cn.superhuang.data.scalpel.actuator;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import cn.superhuang.data.scalpel.actuator.canvas.executor.CanvasExecutor;
import cn.superhuang.data.scalpel.app.task.model.TaskResultSummary;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;

public class ActuatorApplication {

    public static void main(String[] args) throws JsonProcessingException {
        String endpoint = args[0];
        String taskId = args[1];
        String taskInstanceId = args[2];
        AdminClient adminClient = new AdminClient(endpoint);

        TaskResult taskResult = new TaskResult();
        try {
            TaskConfiguration taskConfiguration = adminClient.getTaskContent(taskId, taskInstanceId);
            ActuatorContext actuatorContext = ActuatorContext.getOrCreate(taskConfiguration);
            actuatorContext.setAdminClient(adminClient);

            actuatorContext.log("我要开始了");

            CanvasExecutor canvasExecutor = new CanvasExecutor(actuatorContext);
            TaskResultSummary taskResultSummary = canvasExecutor.execute();

//            String driver = "org.postgresql.Driver";
//            String url = "jdbc:postgresql://10.0.0.172:5432/sde02?stringtype=unspecified";
//            String table = "sys_user";
//            String username = "postgres";
//            String password = "Gistack@123";
//
//            Dataset<Row> dataset = actuatorContext.getSparkSession().read()
//                    .format("jdbc")
//                    .option("driver", driver)
//                    .option("url", url)
//                    .option("dbtable", "\"ATT_CWS_BASE\"")
//                    .option("user", username)
//                    .option("password", password)
//                    .load();
//            dataset.printSchema();
//            dataset.show();

            actuatorContext.log("我要又结束了");

            taskResult.setSuccess(true);
            actuatorContext.destory();
        } catch (Exception e) {
            e.printStackTrace();
            taskResult.setSuccess(false);
            taskResult.setMessage(ExceptionUtil.getSimpleMessage(e));
            taskResult.setDetail(ExceptionUtil.stacktraceToString(e, -1));
        }
        adminClient.sendTaskResult(taskId, taskInstanceId, taskResult);
    }
}
