package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Role;
import cn.superhuang.data.scalpel.admin.app.sys.domain.User;
import cn.superhuang.data.scalpel.admin.app.sys.model.enumeration.UserState;
import cn.superhuang.data.scalpel.admin.app.sys.repository.RoleRepository;
import cn.superhuang.data.scalpel.admin.app.sys.repository.UserRepository;
import cn.superhuang.data.scalpel.admin.app.sys.service.UserService;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.UserChangePasswordRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.UserCreateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.UserUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.vo.UserVO;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserResource extends BaseResource implements IUserResource {

    @Resource
    private UserService userService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private RoleRepository roleRepository;

    @Override
    public GenericResponse<Page<UserVO>> search(GenericSearchRequestDTO searchRequest) {
        Specification<User> spec = resolveSpecification(searchRequest.getSearch(), User.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<User> page = userRepository.findAll(spec, pageRequest);
        Page<UserVO> result = new PageImpl<>(BeanUtil.copyToList(page.getContent(), UserVO.class), page.getPageable(), page.getTotalElements());

        Map<String, Role> roleMap = roleRepository.findAll().stream().collect(Collectors.toMap(Role::getId, r -> r));
        result.getContent().stream().filter(u->u.getRoleId()!=null).forEach(u->u.setRoleName(roleMap.get(u.getRoleId()).getName()));
        return GenericResponse.ok(result);
    }

    @Override
    public GenericResponse<UserVO> create(UserCreateRequest createRequest) throws Exception {
        User user = BeanUtil.copyProperties(createRequest, User.class);
        userService.addUser(user);
        return GenericResponse.ok(BeanUtil.copyProperties(user, UserVO.class));
    }

    @Override
    public GenericResponse<Void> update(String id, UserUpdateRequest updateRequest) throws URISyntaxException {
        User user = BeanUtil.copyProperties(updateRequest, User.class);
        user.setId(id);
        userService.updateUserBaseInfo(user);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(String id) {
        userService.deleteUser(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> enable(String id) throws URISyntaxException {
        User user = new User();
        user.setId(id);
        user.setState(UserState.ENABLE);
        userService.updateUserBaseInfo(user);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> disable(String id) throws URISyntaxException {
        User user = new User();
        user.setId(id);
        user.setState(UserState.DISABLE);
        userService.updateUserBaseInfo(user);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> disable(String id, UserChangePasswordRequest changePasswordRequest) throws URISyntaxException {
        userService.changePassword(id, changePasswordRequest.getPassword());
        return GenericResponse.ok();
    }
}
