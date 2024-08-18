package cn.superhuang.data.scalpel.admin.app.task.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.task.domain.Task;
import cn.superhuang.data.scalpel.admin.app.task.repository.TaskRepository;
import cn.superhuang.data.scalpel.admin.app.task.service.TaskService;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskContentValidateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.TaskUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.model.dto.TaskDTO;
import cn.superhuang.data.scalpel.admin.model.dto.TaskUpdateDTO;
import cn.superhuang.data.scalpel.admin.model.dto.ValidateResultDTO;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.model.web.vo.TaskDetailVO;
import cn.superhuang.data.scalpel.admin.model.web.vo.TaskListItemVO;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class TaskResource extends BaseResource implements ITaskResource {
	@Resource
	private TaskRepository taskRepository;
	@Resource
	private TaskService taskService;

	@Override
	public GenericResponse<Page<TaskListItemVO>> search(GenericSearchRequestDTO searchRequest) {
		Specification<Task> spec = resolveSpecification(searchRequest.getSearch(), Task.class);
		PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
		Page<Task> page = taskRepository.findAll(spec, pageRequest);
		List<TaskListItemVO> listVo = BeanUtil.copyToList(page.getContent(), TaskListItemVO.class);
		Page<TaskListItemVO> result = new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
		return GenericResponse.ok(result);
	}

	@Override
	public GenericResponse<Void> createTask(TaskCreateRequestVO createRequest) throws Exception {
		TaskDTO taskDTO = BeanUtil.copyProperties(createRequest, TaskDTO.class);
		taskService.save(taskDTO);
		return GenericResponse.ok();
	}

	@Override
	public GenericResponse<TaskDetailVO> getTask(String id) {
		Optional<TaskDetailVO> taskDetailVO = taskRepository.findById(id).map(task -> BeanUtil.copyProperties(task, TaskDetailVO.class));
		return GenericResponse.wrapOrNotFound(taskDetailVO);
	}

	@Override
	public GenericResponse<Void> updateTask(String id, TaskUpdateRequestVO updateRequest) throws Exception {
		TaskUpdateDTO taskUpdateDTO = BeanUtil.copyProperties(updateRequest, TaskUpdateDTO.class);
		taskUpdateDTO.setId(id);
		taskService.update(taskUpdateDTO);
		return GenericResponse.ok();
	}

	@Override
	public GenericResponse<Void> deleteTask(String id) throws Exception {
		taskService.delete(id);
		return GenericResponse.ok();
	}

	@Override
	public GenericResponse<Void> enableTask(String id) throws Exception {
		taskService.enableTask(id);
		return GenericResponse.ok();
	}

	@Override
	public GenericResponse<Void> disableTask(String id) throws Exception {
		taskService.disableTask(id);
		return GenericResponse.ok();
	}

	@Override
	public GenericResponse<ValidateResultDTO> validateTaskContent(TaskContentValidateRequestVO validateRequest) throws Exception {
		ValidateResultDTO validateResultDTO = new ValidateResultDTO();
		validateResultDTO.setVail(true);
		return GenericResponse.ok(validateResultDTO);

	}

	@Override
	public GenericResponse<Void> runOnceTask(String id) throws Exception {
		taskService.runTask(id, new Date());
		return GenericResponse.ok();
	}
}