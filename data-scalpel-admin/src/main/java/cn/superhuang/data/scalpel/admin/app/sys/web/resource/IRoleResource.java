package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Role;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.RoleCreateRequest;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.RoleUpdateRequest;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Validated
@Tag(name = "01.系统管理-角色管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "01", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IRoleResource {
    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/roles")
    @ResponseBody
    public GenericResponse<Page<Role>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @PostMapping("/roles")
    public GenericResponse<Role> create(@Valid @RequestBody RoleCreateRequest createRequest) throws Exception;

    @Operation(summary = "修改", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PutMapping("/roles/{id}")
    public GenericResponse<Void> update(
            @PathVariable(value = "id", required = true) @NotNull final String id,
            @RequestBody RoleUpdateRequest updateRequest
    ) throws URISyntaxException;

    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @DeleteMapping("/roles/{id}")
    public GenericResponse<Void> delete(@PathVariable(value = "id", required = true) @NotNull String id);
}
