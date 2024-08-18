package cn.superhuang.data.scalpel.admin.repository;

import cn.superhuang.data.scalpel.admin.app.task.domain.TaskScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Script entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScriptRepository extends JpaRepository<TaskScript, String> , JpaSpecificationExecutor<TaskScript> {
}
