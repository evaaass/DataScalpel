package cn.superhuang.data.scalpel.admin.app.sys.repository;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Catalog;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Catalog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogRepository extends JpaRepository<Catalog, String>, JpaSpecificationExecutor<Datasource> {

    public List<Catalog> findAllByTypeAndParentId(String type, String parentId);

    public List<Catalog> findAllByType(String type);

    public Long countByTypeAndParentId(String type, String parentId);
}
