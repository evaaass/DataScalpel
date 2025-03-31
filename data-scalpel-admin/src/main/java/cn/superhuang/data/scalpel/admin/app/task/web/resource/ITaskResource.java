package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.model.CanvasPreRunSummary;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.CanvasPreRunRequest;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskDefinitionUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskUpdateRequestVO;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Tag(name = "40.任务管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "40", parseValue = true)})})
@RequestMapping("/api/v1")
public interface ITaskResource {
    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/tasks")
    public GenericResponse<Page<Task>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @PostMapping("/tasks")
    public GenericResponse<Void> createTask(@RequestBody TaskCreateRequestVO createRequest) throws Exception;

    @Operation(summary = "详情", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @GetMapping("/tasks/{id}")
    public GenericResponse<Task> getTask(@PathVariable("id") String id);

    @Operation(summary = "更新", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @PutMapping("/tasks/{id}")
    public GenericResponse<Void> updateTask(@PathVariable("id") String id, @RequestBody TaskUpdateRequestVO createRequest) throws Exception;

    @Operation(summary = "更新任务定义", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "5", parseValue = true)})})
    @PutMapping("/tasks/{id}/definition")
    public GenericResponse<Void> updateTaskConfiguration(@PathVariable("id") String id, @RequestBody TaskDefinitionUpdateRequestVO updateRequest) throws Exception;

    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "6", parseValue = true)})})
    @DeleteMapping("/tasks/{id}")
    public GenericResponse<Void> deleteTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "启用", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "7", parseValue = true)})})
    @PostMapping("/tasks/{id}/actions/enable")
    public GenericResponse<Void> enableTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "禁用", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "8", parseValue = true)})})
    @PostMapping("/tasks/{id}/actions/disable")
    public GenericResponse<Void> disableTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "立即运行", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "9", parseValue = true)})})
    @PostMapping("/tasks/{id}/actions/run-once")
    public GenericResponse<Void> runOnceTask(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "画布任务预运行", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "30", parseValue = true)})})
    @PostMapping("/tasks/actions/canvas-pre-run")
    GenericResponse<Collection<CanvasPreRunSummary>> canvasPreRun(@RequestBody CanvasPreRunRequest preRunRequest) throws Exception;

}
