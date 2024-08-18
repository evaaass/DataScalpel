package cn.superhuang.data.scalpel.admin.app.sys.repository;

import cn.superhuang.data.scalpel.admin.app.sys.domain.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Datasource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SysLogRepository extends JpaRepository<SysLog, String> , JpaSpecificationExecutor<SysLog>{}
