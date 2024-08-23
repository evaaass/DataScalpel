package cn.superhuang.data.scalpel.admin.app.dispatcher.model;

import cn.superhuang.data.scalpel.admin.app.dispatcher.model.enums.TaskRunnerInstanceState;
import lombok.Data;

@Data
public class TaskRunnerInstanceInfo {

    private TaskRunnerInstanceState state;

    private String message;

    private String detail;
}
