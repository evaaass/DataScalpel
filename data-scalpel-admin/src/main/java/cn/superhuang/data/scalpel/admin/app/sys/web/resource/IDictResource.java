package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.DictCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.DictUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
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
@Tag(name = "03.系统管理-字典管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "03", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IDictResource {

    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/dict")
    @ResponseBody
    public GenericResponse<Page<Dict>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @PostMapping("/dict")
    public GenericResponse<Dict> create(@Valid @RequestBody DictCreateRequestVO createRequest) throws Exception;

    @Operation(summary = "修改", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PutMapping("/dict/{id}")
    public GenericResponse<Void> update(
            @PathVariable(value = "id", required = true) @NotNull final String id,
            @RequestBody DictUpdateRequestVO updateRequest
    ) throws URISyntaxException;


    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @DeleteMapping("/dict/{id}")
    public GenericResponse<Void> delete(@PathVariable @NotNull String id);
}
