package cn.superhuang.data.scalpel.admin.app.sys.repository;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import cn.superhuang.data.scalpel.admin.app.sys.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Catalog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    public Optional<User> findByName(String username);

    public Integer countByRoleId(String roleId);
}
