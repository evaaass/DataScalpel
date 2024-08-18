package cn.superhuang.data.scalpel.admin.app.datasource.repository;

import cn.superhuang.data.scalpel.admin.app.datasource.domain.DatasourceDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DatasourceDefinition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DatasourceDefinitionRepository extends JpaRepository<DatasourceDefinition, String> {}
