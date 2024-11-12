package cn.superhuang.data.scalpel.admin.app.task.repository;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskInstance;
import cn.superhuang.data.scalpel.model.enumeration.TaskInstanceExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the TaskInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, String>, JpaSpecificationExecutor<TaskInstance> {

    List<TaskInstance> findAllByTaskId(String taskId);

    Optional<TaskInstance> findTaskInstanceByChannelId(String channelId);

    Long countAllByTaskIdAndStatus(String taskId, TaskInstanceExecutionStatus status);
}
