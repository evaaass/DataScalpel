package cn.superhuang.data.scalpel.actuator;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.superhuang.data.scalpel.app.sys.model.SysLogCreateDTO;
import cn.superhuang.data.scalpel.app.sys.model.emun.LogTargetType;
import cn.superhuang.data.scalpel.app.task.model.TaskResultSummary;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import lombok.Data;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class ActuatorContext {
    private static ActuatorContext actuatorContext;
    private TaskConfiguration taskConfiguration;
    private ObjectMapper objectMapper;
    private SparkSession sparkSession;
    private TaskResultSummary taskResultSummary;
    private JavaSparkContext javaSparkContext;
    private AdminClient adminClient;

    private ActuatorContext() {
    }

    public synchronized static ActuatorContext getOrCreate(TaskConfiguration taskConfiguration) {
        if (actuatorContext != null) {
            return actuatorContext;
        }

        ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));


        AtomicReference<SparkConf> sparkConfRef = new AtomicReference();
        sparkConfRef.set(new SparkConf());

        taskConfiguration.getSparkConfiguration().getConfigs().forEach((key, value) -> {
            SparkConf sparkConf = sparkConfRef.get().set(key, value);
            sparkConfRef.set(sparkConf);
        });
        SparkConf sparkConf = sparkConfRef.get();
        if (StrUtil.isNotBlank(taskConfiguration.getSparkConfiguration().getMaster())) {
            sparkConf = sparkConf.setMaster(taskConfiguration.getSparkConfiguration().getMaster());
        }
        SparkSession sparkSession = SparkSession.builder()
                .config(sparkConf)
                .config("spark.sql.codegen.wholeStage", "false")
                .config("spark.sql.crossJoin.enabled", "true")
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        JavaSparkContext javaSparkContext = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());

        ActuatorContext context = new ActuatorContext();
        context.setTaskConfiguration(taskConfiguration);
        context.setObjectMapper(objectMapper);
        context.setSparkSession(sparkSession);
        context.setJavaSparkContext(javaSparkContext);
        actuatorContext = context;
        return context;
    }

    public void log(String message) {
        try {
            SysLogCreateDTO sysLogCreateDTO = new SysLogCreateDTO();
            sysLogCreateDTO.setLogTargetType(LogTargetType.TASK_INSTANCE);
            sysLogCreateDTO.setLogTargetId(taskConfiguration.getTaskInstanceId());
            sysLogCreateDTO.setLevel(LogLevel.INFO);
            sysLogCreateDTO.setMessage(message);
            sysLogCreateDTO.setLogTime(LocalDateTime.now());
            adminClient.sendLog(sysLogCreateDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void destory() {
        this.getSparkSession().stop();
    }
}
