package cn.superhuang.data.scalpel.admin.app.sys.model;

import cn.superhuang.data.scalpel.admin.app.sys.model.enumeration.UserState;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
public class UserDetail implements UserDetails, Serializable {
    private String id;

    private String name;

    private String nickName;

    private String password;

    private String salt;

    private String email;

    private String phone;

    private UserState state;

    private String roleId;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return name;
    }
}
