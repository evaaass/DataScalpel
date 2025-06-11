package cn.superhuang.data.scalpel.apiserver.resource;

import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@Validated
@ApiSupport(order = 10)
@Tag(name = "10.服务管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
public interface IServiceResource {

    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/services")
    GenericResponse<Page<Service>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "服务测试", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @PostMapping("/services/actions/test")
    public GenericResponse<ServiceTestResult> testService(@RequestBody ServiceDTO service, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "注册服务", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @PostMapping("/services")
    public GenericResponse<Void> upService(@RequestBody ServiceDTO service) throws Exception;

    @Operation(summary = "删除服务", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @DeleteMapping("/services/down")
    public GenericResponse<Void> downService(@RequestBody ServiceDTO service);
}