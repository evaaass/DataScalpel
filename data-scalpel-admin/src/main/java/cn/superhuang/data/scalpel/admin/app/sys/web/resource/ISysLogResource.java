package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.domain.SysLog;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Tag(name = "04.系统管理-日志模块", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "04", parseValue = true)})})
@RequestMapping("/api/v1")
public interface ISysLogResource {
    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/sys-log")
    GenericResponse<Page<SysLog>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

//    @Operation(summary = "增加日志")
//    @PostMapping("/sys-log")
//    GenericResponse<Void> add(@RequestBody SysLogCreateRequest createRequest);

}
