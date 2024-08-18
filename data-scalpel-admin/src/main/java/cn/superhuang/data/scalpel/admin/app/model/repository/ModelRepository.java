package cn.superhuang.data.scalpel.admin.app.model.repository;

import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Repository
public interface ModelRepository extends JpaRepository<Model, String>, JpaSpecificationExecutor<Model> {

    Boolean existsModelByDatasourceIdAndName(String datasourceId, String name);

    List<Model> findAllByIdIn(Set<String> ids);
}
