package cn.superhuang.data.scalpel.admin.app.model.repository;

import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@SuppressWarnings("unused")
@Repository
public interface ModelFieldRepository extends JpaRepository<ModelField, String>, JpaSpecificationExecutor<ModelField> {

    List<ModelField> findAllByModelId(String modelId);

    void deleteAllByModelId(String modelId);

}
