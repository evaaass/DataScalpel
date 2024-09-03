package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "41.任务实例管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "41", parseValue = true)})})
@RequestMapping("/api/v1")
public interface ITaskInstanceResource {
    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/tasks/{taskId}/instances")
    public GenericResponse<Page<TaskInstance>> search(@PathVariable("taskId") String taskId, @ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "详情", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/tasks/{taskId}/instances/{taskInstanceId}")
    public GenericResponse<TaskInstance> detail(@PathVariable("taskId") String taskId, @PathVariable("taskInstanceId") String taskInstanceId);

    @Operation(summary = "日志", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @GetMapping("/tasks/{taskId}/instances/{taskInstanceId}/logs")
    public GenericResponse<List<TaskLog>> getLogs(@PathVariable("taskId") String taskId, @PathVariable("taskInstanceId") String taskInstanceId) throws Exception;


    @Operation(summary = "日志-控制台", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "11", parseValue = true)})})
    @GetMapping("/tasks/{taskId}/instances/{taskInstanceId}/console-logs")
    public GenericResponse<String> getConsoleLogs(@PathVariable("taskId") String taskId, @PathVariable("taskInstanceId") String taskInstanceId) throws Exception;


    @Operation(summary = "日志-控制台下载", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "11", parseValue = true)})})
    @GetMapping("/tasks/{taskId}/instances/{taskInstanceId}/download-console-logs")
    public ResponseEntity<InputStreamResource> downloadConsoleLogs(@PathVariable("taskId") String taskId, @PathVariable("taskInstanceId") String taskInstanceId) throws Exception;


}
