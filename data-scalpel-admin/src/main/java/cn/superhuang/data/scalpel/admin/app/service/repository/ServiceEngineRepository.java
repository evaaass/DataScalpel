
package cn.superhuang.data.scalpel.admin.app.service.repository;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.domain.ServiceEngine;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceEngineRepository extends JpaRepository<ServiceEngine, String>, JpaSpecificationExecutor<ServiceEngine> {
}
