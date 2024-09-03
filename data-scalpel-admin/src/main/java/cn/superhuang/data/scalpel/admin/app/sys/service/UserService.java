package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.extra.spring.SpringUtil;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Role;
import cn.superhuang.data.scalpel.admin.app.sys.domain.User;
import cn.superhuang.data.scalpel.admin.app.sys.model.UserDetail;
import cn.superhuang.data.scalpel.admin.app.sys.model.enumeration.UserState;
import cn.superhuang.data.scalpel.admin.app.sys.repository.RoleRepository;
import cn.superhuang.data.scalpel.admin.app.sys.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username).orElseThrow(() -> {
            throw new UsernameNotFoundException(username);
        });
        Role role = roleRepository.getReferenceById(user.getRoleId());
        UserDetail userDetail = BeanUtil.copyProperties(user, UserDetail.class);
        userDetail.setAuthorities(role.getPermissions().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        return userDetail;
    }

    public void addUser(User user) {
        user.setPassword(SpringUtil.getBean(PasswordEncoder.class).encode(user.getPassword()));
        user.setState(UserState.ENABLE);
        userRepository.save(user);
    }

    public void updateUserBaseInfo(User user) {
        userRepository.findById(user.getId()).ifPresentOrElse(po -> {
            BeanUtil.copyProperties(user, po, CopyOptions.create().ignoreNullValue());
            userRepository.save(po);
        }, () -> {
            throw new RuntimeException("用户不存在");
        });
    }

    //TODO 密码处理加密问题
    public void changePassword(String id, String password) {
        userRepository.findById(id).ifPresentOrElse(po -> {
            po.setPassword(SpringUtil.getBean(PasswordEncoder.class).encode(password));
            userRepository.save(po);
        }, () -> {
            throw new RuntimeException("用户不存在");
        });
    }

    //TODO 逻辑删除，用户不做物理删除
    public void deleteUser(String id) {
        userRepository.findById(id).ifPresentOrElse(po -> {
            po.setState(UserState.DELETED);
            userRepository.save(po);
        }, () -> {
            throw new RuntimeException("用户不存在");
        });
    }
}
