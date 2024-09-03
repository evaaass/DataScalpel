package cn.superhuang.data.scalpel.admin.app.task.service.interceptor;

import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface TaskSubmitInterceptor {

    public void beforeSubmit(TaskConfiguration taskConfiguration) throws JsonProcessingException;
}
