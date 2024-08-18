package cn.superhuang.data.scalpel.actuator;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import cn.superhuang.data.scalpel.actuator.util.JsonUtil;
import cn.superhuang.data.scalpel.app.sys.model.SysLogCreateDTO;
import cn.superhuang.data.scalpel.app.task.model.TaskInstancePlan;
import cn.superhuang.data.scalpel.model.task.TaskResult;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import cn.superhuang.data.scalpel.model.web.GenericResponse;

public class AdminClient {


    private String endpoint;

    public AdminClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public TaskConfiguration getTaskContent(String taskId, String taskInstanceId) throws JsonProcessingException {
        String url = endpoint + "/api/v1/tasks/" + taskId + "/instances/" + taskInstanceId + "/plan";
        String taskContentString = HttpUtil.get(url);
        GenericResponse<TaskInstancePlan> res = JsonUtil.objectMapper.readValue(taskContentString, new TypeReference<GenericResponse<TaskInstancePlan>>() {
        });
        return res.getData().getTaskConfiguration();
    }

    public void sendTaskResult(String taskId, String taskInstanceId, TaskResult taskResult) throws JsonProcessingException {
        String url = endpoint + "/api/v1/tasks/" + taskId + "/instances/" + taskInstanceId + "/actions/finish";
        String res=HttpUtil.post(url, JsonUtil.objectMapper.writeValueAsString(taskResult));
        System.out.println(res);
    }

    public void sendLog(SysLogCreateDTO sysLogCreateDTO) throws JsonProcessingException {
        String url = endpoint + "/api/v1/sys-log";
        System.out.println(JsonUtil.objectMapper.writeValueAsString(sysLogCreateDTO));
        String res = HttpUtil.post(url, JsonUtil.objectMapper.writeValueAsString(sysLogCreateDTO));
        System.out.println(res);
    }
}
