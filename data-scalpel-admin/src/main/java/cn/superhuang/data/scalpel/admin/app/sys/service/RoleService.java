package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.app.sys.repository.RoleRepository;
import cn.superhuang.data.scalpel.admin.app.sys.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Resource
    private RoleRepository roleRepository;
    @Resource
    private UserRepository userRepository;

    public void delete(String id) {
        roleRepository.findById(id).ifPresent(po -> {
            Integer count = userRepository.countByRoleId(id);
            if (count > 0) {
                throw new RuntimeException("角色下面还有用户，无法删除");
            }
            roleRepository.deleteById(id);
        });
    }
}
