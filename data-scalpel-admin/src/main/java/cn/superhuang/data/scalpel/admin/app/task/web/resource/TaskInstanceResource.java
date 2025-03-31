package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.hutool.core.io.IoUtil;
import cn.superhuang.data.scalpel.admin.app.task.service.TaskInstanceLogService;
import cn.superhuang.data.scalpel.admin.app.task.service.TaskManagerService;
import cn.superhuang.data.scalpel.model.web.GenericResponse;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceRepository;
import cn.superhuang.data.scalpel.impl.BaseResource;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
public class TaskInstanceResource extends BaseResource implements ITaskInstanceResource {
    @Resource
    private TaskInstanceRepository repository;

    @Resource
    private TaskManagerService taskManagerService;

    @Resource
    private TaskInstanceLogService  taskInstanceLogService;

    @Override
    public GenericResponse<Page<TaskInstance>> search(String taskId, GenericSearchRequestDTO searchRequest) {
        Specification<TaskInstance> spec = resolveSpecification(searchRequest.getSearch(), TaskInstance.class);
        spec = spec.and((Specification) (root, query, builder) -> builder.equal(root.get("taskId"), taskId));
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<TaskInstance> page = repository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse<TaskInstance> detail(String taskId, String id) {
        Optional<TaskInstance> result = repository.findById(id);
        return GenericResponse.wrapOrNotFound(result);
    }

    @Override
    public GenericResponse<List<TaskLog>> getLogs(String taskId, String id) throws Exception {
        List<TaskLog> logs = taskInstanceLogService.getTaskLog(taskId, id);
        return GenericResponse.ok(logs);
    }

    @Override
    public GenericResponse<String> getConsoleLogs(String taskId, String taskInstanceId) throws Exception {
        InputStream inputStream = taskInstanceLogService.getTaskConsoleLog(taskId, taskInstanceId);
        return GenericResponse.ok(IoUtil.readUtf8(inputStream));
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadConsoleLogs(String taskId, String taskInstanceId) throws Exception {
        InputStream inputStream = taskInstanceLogService.getTaskConsoleLog(taskId, taskInstanceId);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + taskInstanceId + ".log")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
