package cn.superhuang.data.scalpel.admin.app.dispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTriggerResult {
    private Boolean success;
    private String taskId;
    private String taskInstanceId;
    private String channelId;
}