package cn.superhuang.data.scalpel.admin.app.datasource.repository;

import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Datasource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, String> , JpaSpecificationExecutor<Datasource>{

    List<Datasource> findAllByIdIn(Set<String> ids);
}
