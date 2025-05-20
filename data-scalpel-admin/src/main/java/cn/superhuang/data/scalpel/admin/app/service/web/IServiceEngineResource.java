package cn.superhuang.data.scalpel.admin.app.service.web;

import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelFieldUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelDetailVO;
import cn.superhuang.data.scalpel.admin.app.service.domain.ServiceEngine;
import cn.superhuang.data.scalpel.admin.app.service.web.request.ServiceEngineCreateRequest;
import cn.superhuang.data.scalpel.admin.app.service.web.request.ServiceEngineTestRequest;
import cn.superhuang.data.scalpel.admin.app.service.web.request.ServiceEngineUpdateRequest;
import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "95.服务引擎管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "95", parseValue = true)})})
@RequestMapping("/api/v1/service-engines")
public interface IServiceEngineResource {

    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("")
    @ResponseBody
    GenericResponse<Page<ServiceEngine>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PostMapping("")
    GenericResponse<ServiceEngine> create(@RequestBody ServiceEngineCreateRequest createRequest) throws Exception;

    @Operation(summary = "更新", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @PutMapping("/{id}")
    GenericResponse<Void> update(@PathVariable("id") String id, @RequestBody ServiceEngineUpdateRequest updateRequest) throws Exception;

    @ResponseBody
    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "5", parseValue = true)})})
    @DeleteMapping("/models/{id}")
    GenericResponse<Void> delete(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "测试连接", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @PostMapping("/actions/test")
    GenericResponse<TestResult> test(@RequestBody ServiceEngineTestRequest testRequest) throws Exception;
}
