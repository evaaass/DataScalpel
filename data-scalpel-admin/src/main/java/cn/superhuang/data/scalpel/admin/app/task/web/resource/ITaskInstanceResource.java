package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务实例管理")
@RequestMapping("/api/v1")
public interface ITaskInstanceResource {
    @Operation(summary = "获取任务实例列表")
    @GetMapping("/tasks/{taskId}/instances")
    public GenericResponse<Page<TaskInstance>> search(@PathVariable("taskId") String taskId, @ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "获取任务实例详情")
    @GetMapping("/tasks/{taskId}/instances/{id}")
    public GenericResponse<TaskInstance> detail(@PathVariable("taskId") String taskId, @PathVariable("id") String id);

    @Operation(summary = "获取任务实例日志")
    @GetMapping("/tasks/{taskId}/instances/{id}/logs")
    public GenericResponse<Page<TaskLog>> searchLogs(@PathVariable("taskId") String taskId, @PathVariable("id") String id);

//    @Operation(summary = "获取任务计划（内部接口）")
//    @GetMapping("/tasks/{taskId}/instances/{id}/plan")
//    public GenericResponse<TaskInstancePlan> getInstancePlan(@PathVariable("taskId") String taskId, @PathVariable("id") String id);

//    @Operation(summary = "启用任务")
//    @PostMapping("/tasks/{taskId}/instances/{id}/actions/finish")
//    public GenericResponse<Void> finishInstance(@PathVariable("taskId") String taskId, @PathVariable("id") String id, @RequestBody TaskResult taskResult) throws Exception;


}
