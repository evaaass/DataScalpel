package cn.superhuang.data.scalpel.admin.repository;

import cn.superhuang.data.scalpel.admin.app.item.domain.MetadataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetadataTypeRepository extends JpaRepository<MetadataType, String>, JpaSpecificationExecutor<MetadataType> {

}