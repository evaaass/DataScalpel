package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Role;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.RoleCreateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.RoleUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.UserCreateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.UserUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.vo.UserVO;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Validated
@ApiSupport(order = 10)
@Tag(name = "01.系统管理-角色管理")
@RequestMapping("/api/v1")
public interface IRoleResource {
    @ApiOperationSupport(order = 0)
    @Operation(summary = "列表")
    @GetMapping("/roles")
    @ResponseBody
    public GenericResponse<Page<Role>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建")
    @PostMapping("/roles")
    public GenericResponse<Role> create(@Valid @RequestBody RoleCreateRequest createRequest) throws Exception;

    @Operation(summary = "修改")
    @PutMapping("/roles/{id}")
    public GenericResponse<Void> update(
            @PathVariable(value = "id", required = true) @NotNull final String id,
            @RequestBody RoleUpdateRequest updateRequest
    ) throws URISyntaxException;

    @Operation(summary = "删除")
    @DeleteMapping("/roles/{id}")
    public GenericResponse<Void> delete(@PathVariable @NotNull String id);
}
