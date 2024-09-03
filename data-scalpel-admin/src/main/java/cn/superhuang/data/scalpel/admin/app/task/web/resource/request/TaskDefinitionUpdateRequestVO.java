package cn.superhuang.data.scalpel.admin.app.task.web.resource.request;

import cn.superhuang.data.scalpel.admin.model.enumeration.TaskScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class TaskDefinitionUpdateRequestVO {
    private String definition;
}