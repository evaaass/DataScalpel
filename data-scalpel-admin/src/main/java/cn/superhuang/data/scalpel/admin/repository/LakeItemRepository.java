package cn.superhuang.data.scalpel.admin.repository;

import cn.superhuang.data.scalpel.admin.app.item.domain.LakeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LakeItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LakeItemRepository extends JpaRepository<LakeItem, String>  , JpaSpecificationExecutor<LakeItem> {}
