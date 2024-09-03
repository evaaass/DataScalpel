package cn.superhuang.data.scalpel.admin.app.service.web;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceDetail;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceTestResult;
import cn.superhuang.data.scalpel.admin.app.service.web.request.RestServiceCreateRequest;
import cn.superhuang.data.scalpel.admin.app.service.web.request.RestServiceTestRequest;
import cn.superhuang.data.scalpel.admin.app.service.web.request.RestServiceUpdateRequest;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Validated
@Tag(name = "90.服务管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "90", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IRestServiceResource {

    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/services")
    GenericResponse<Page<RestService>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @PostMapping("/services")
    GenericResponse<RestService> create(@Valid @RequestBody RestServiceCreateRequest createRequest) throws Exception;

    @Operation(summary = "修改", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PutMapping("/services/{id}")
    GenericResponse<Void> update(
            @PathVariable(value = "id", required = false) final String id,
            @Valid @RequestBody RestServiceUpdateRequest updateRequest
    ) throws URISyntaxException;

    @Operation(summary = "详情", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @GetMapping("/services/{id}")
    GenericResponse<RestServiceDetail> getDetail(@PathVariable(value = "id") String id);

    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "5", parseValue = true)})})
    @DeleteMapping("/services/{id}")
    GenericResponse<Void> delete(@PathVariable("id") String id);

    @Operation(summary = "测试", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @PostMapping("/services/{id}/actions/test")
    GenericResponse<RestServiceTestResult> test(@RequestBody RestServiceTestRequest testRequest);

    @Operation(summary = "上线", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "12", parseValue = true)})})
    @PostMapping("/services/{id}/actions/online")
    GenericResponse<Void> online(@PathVariable("id") String id);

    @Operation(summary = "下线", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "12", parseValue = true)})})
    @PostMapping("/services/{id}/actions/offline")
    GenericResponse<Void> offline(@PathVariable("id") String id);
}
