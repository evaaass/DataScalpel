package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Role;
import cn.superhuang.data.scalpel.admin.app.sys.repository.RoleRepository;
import cn.superhuang.data.scalpel.admin.app.sys.service.RoleService;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.RoleCreateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.RoleUpdateRequest;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class RoleResource extends BaseResource implements IRoleResource {

    @Resource
    private RoleService roleService;
    @Resource
    private RoleRepository roleRepository;

    @Override
    public GenericResponse<Page<Role>> search(GenericSearchRequestDTO searchRequest) {
        Specification<Role> spec = resolveSpecification(searchRequest.getSearch(), Role.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<Role> page = roleRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse<Role> create(RoleCreateRequest createRequest) throws Exception {
        Role role = BeanUtil.copyProperties(createRequest, Role.class);
        roleRepository.save(role);
        return GenericResponse.ok(role);
    }

    @Override
    public GenericResponse<Void> update(String id, RoleUpdateRequest updateRequest) throws URISyntaxException {
        roleRepository.findById(id).ifPresentOrElse(po -> {
            BeanUtil.copyProperties(updateRequest, po, CopyOptions.create().ignoreNullValue());
            roleRepository.save(po);
        }, () -> {
            throw new RuntimeException("角色不存在");
        });
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(String id) {
        roleService.delete(id);
        return GenericResponse.ok();
    }
}
