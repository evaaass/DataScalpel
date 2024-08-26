package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.*;
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
@ApiSupport(order = 9)
@Tag(name = "02.系统管理-用户管理")
@RequestMapping("/api/v1")
public interface IUserResource {
    @ApiOperationSupport(order = 0)
    @Operation(summary = "列表")
    @GetMapping("/users")
    @ResponseBody
    public GenericResponse<Page<UserVO>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建")
    @PostMapping("/users")
    public GenericResponse<UserVO> create(@Valid @RequestBody UserCreateRequest createRequest) throws Exception;

    @Operation(summary = "修改")
    @PutMapping("/users/{id}")
    public GenericResponse<Void> update(
            @PathVariable(value = "id", required = true) @NotNull final String id,
            @RequestBody UserUpdateRequest updateRequest
    ) throws URISyntaxException;

    @Operation(summary = "删除")
    @DeleteMapping("/users/{id}")
    public GenericResponse<Void> delete(@PathVariable @NotNull String id);


    @Operation(summary = "启用")
    @PostMapping("/users/{id}/actions/enable")
    public GenericResponse<Void> enable(
            @PathVariable(value = "id", required = true) @NotNull final String id
    ) throws URISyntaxException;

    @Operation(summary = "禁用")
    @PostMapping("/users/{id}/actions/disable")
    public GenericResponse<Void> disable(
            @PathVariable(value = "id", required = true) @NotNull final String id
    ) throws URISyntaxException;

    @Operation(summary = "禁用")
    @PostMapping("/users/{id}/actions/change-password")
    public GenericResponse<Void> disable(
            @PathVariable(value = "id", required = true) @NotNull final String id, @RequestBody UserChangePasswordRequest changePasswordRequest
    ) throws URISyntaxException;
}