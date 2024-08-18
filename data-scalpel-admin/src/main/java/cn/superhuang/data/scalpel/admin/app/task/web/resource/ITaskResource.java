package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskContentValidateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.model.dto.ValidateResultDTO;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.model.web.vo.TaskDetailVO;
import cn.superhuang.data.scalpel.admin.model.web.vo.TaskListItemVO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务管理")
@RequestMapping("/api/v1")
public interface ITaskResource {
    @Operation(summary = "获取任务列表")
    @GetMapping("/tasks")
    public GenericResponse<Page<TaskListItemVO>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建任务")
    @PostMapping("/tasks")
    public GenericResponse<Void> createTask(@RequestBody TaskCreateRequestVO createRequest) throws Exception;

    @Operation(summary = "获取任务详情")
    @GetMapping("/tasks/{id}")
    public GenericResponse<TaskDetailVO> getTask(@PathVariable("id") String id);

    @Operation(summary = "更新任务")
    @PutMapping("/tasks/{id}")
    public GenericResponse<Void> updateTask(@PathVariable("id") String id, @RequestBody TaskUpdateRequestVO createRequest) throws Exception;

    @Operation(summary = "删除任务")
    @DeleteMapping("/tasks/{id}")
    public GenericResponse<Void> deleteTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "启用任务")
    @PostMapping("/tasks/{id}/actions/enable")
    public GenericResponse<Void> enableTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "禁用任务")
    @PostMapping("/tasks/{id}/actions/disable")
    public GenericResponse<Void> disableTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "验证任务信息")
    @PostMapping("/tasks/actions/validate-content")
    public GenericResponse<ValidateResultDTO> validateTaskContent(@RequestBody TaskContentValidateRequestVO validateRequest) throws Exception;


    @Operation(summary = "立即运行任务")
    @PostMapping("/tasks/{id}/actions/run-once")
    public GenericResponse<Void> runOnceTask(@PathVariable("id") String id) throws Exception;
}
