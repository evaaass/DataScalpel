package cn.superhuang.data.scalpel.admin.app.sys.repository;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Catalog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DictRepository extends JpaRepository<Dict, String>, JpaSpecificationExecutor<Dict> {
    public List<Dict> findAllByType(String type);

    Long countAllByTypeAndParentId(String type, String parentId);
}
