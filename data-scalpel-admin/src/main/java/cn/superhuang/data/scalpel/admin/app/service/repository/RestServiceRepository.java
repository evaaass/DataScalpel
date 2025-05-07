package cn.superhuang.data.scalpel.admin.app.service.repository;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestServiceRepository  extends JpaRepository<RestService, String>, JpaSpecificationExecutor<RestService> {

    List<RestService> findAllByType(RestServiceType type);
}
