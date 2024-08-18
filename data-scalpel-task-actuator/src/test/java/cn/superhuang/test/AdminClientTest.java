package cn.superhuang.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import cn.superhuang.data.scalpel.actuator.AdminClient;
import cn.superhuang.data.scalpel.model.task.TaskResult;

public class AdminClientTest {
    public static void main(String[] args) throws JsonProcessingException {
        AdminClient adminClient = new AdminClient("http://10.0.0.5:8080");
        TaskResult taskResult = new TaskResult();
        taskResult.setSuccess(true);
        adminClient.sendTaskResult("8a8080d88c3fb64d018c3fb6929f0000", "8a8080d88c473d21018c47542a340009",taskResult );

//        SysLogCreateRequest sysLogCreateRequest = new SysLogCreateRequest();
//        sysLogCreateRequest.setLogTargetType(LogTargetType.TASK_INSTANCE);
//        sysLogCreateRequest.setLogTargetId("test");
//        sysLogCreateRequest.setLevel(LogLevel.INFO);
//        sysLogCreateRequest.setMessage("test");
//        sysLogCreateRequest.setDetail("test");
//        sysLogCreateRequest.setLogTime(LocalDateTime.now());
//        adminClient.sendLog(sysLogCreateRequest);

//        TaskConfiguration taskConfiguration = adminClient.getTaskContent("8a8080d88c3fb64d018c3fb6929f0000", "8a8080d88c44eccd018c44fb6e1a0006");
//        System.out.println(JsonUtil.objectMapper.writeValueAsString(taskConfiguration));
    }
}