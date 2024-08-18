package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.superhuang.data.scalpel.admin.app.task.service.TaskService;
import cn.superhuang.data.scalpel.model.web.GenericResponse;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskInstanceRepository;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.model.task.TaskLog;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TaskInstanceResource extends BaseResource implements ITaskInstanceResource {
    @Resource
    private TaskInstanceRepository repository;

    @Resource
    private TaskService taskService;

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
    public GenericResponse<Page<TaskLog>> searchLogs(String taskId, String id) {
        return null;
    }

//    @Override
//    public GenericResponse<TaskInstancePlan> getInstancePlan(String taskId, String id) {
//        return GenericResponse.ok(taskService.getTaskPlan(taskId, id));
//    }
//
//    @Override
//    public GenericResponse<Void> finishInstance(String taskId, String instanceId, TaskResult taskResult) throws Exception {
//        TaskRunningStatus taskRunningStatus = new TaskRunningStatus();
//        taskRunningStatus.setState(taskResult.getSuccess() == true ? TaskInstanceExecutionStatus.SUCCESS : TaskInstanceExecutionStatus.FAILURE);
//        taskRunningStatus.setMessage(taskResult.getMessage());
//        taskRunningStatus.setDetail(taskResult.getDetail());
//        taskService.updateInstanceState(instanceId, taskRunningStatus);
//        return GenericResponse.ok();
//    }
}
