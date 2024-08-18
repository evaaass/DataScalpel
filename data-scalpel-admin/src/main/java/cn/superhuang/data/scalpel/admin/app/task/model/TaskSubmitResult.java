package cn.superhuang.data.scalpel.admin.app.task.model;

import cn.superhuang.data.scalpel.model.GenericResult;
import lombok.Data;

@Data
public class TaskSubmitResult extends GenericResult {

    private String taskInstanceId;
    private String taskInstanceChannelId;


}
