package cn.superhuang.data.scalpel.apiserver.domain.repository;

import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
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
public interface DatasourceRepository extends JpaRepository<Datasource, String>, JpaSpecificationExecutor<Datasource> {

    List<Datasource> findAllByIdIn(Set<String> ids);
}
