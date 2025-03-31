package cn.superhuang.data.scalpel.admin.app.sys.domain;

import cn.superhuang.data.scalpel.admin.app.sys.model.enumeration.UserState;
import cn.superhuang.data.scalpel.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Data
@Entity
@Table(name = "admin_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends AbstractAuditingEntity<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(unique = true)
    private String name;

    private String nickName;

    private String password;

    private String salt;

    private String email;

    private String phone;

    private UserState state;

    private String roleId;
}
