package cn.superhuang.data.scalpel.apiserver.resource;

import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
import cn.superhuang.data.scalpel.apiserver.resource.request.DatasourceCreateRequestVO;
import cn.superhuang.data.scalpel.apiserver.resource.request.DatasourceUpdateRequestVO;
import cn.superhuang.data.scalpel.model.GenericResult;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
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
@ApiSupport(order = 10)
@Tag(name = "01.数据源管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IDatasourceResource {
    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/data-sources")
    GenericResponse<Page<Datasource>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "注册", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PostMapping("/data-sources")
    GenericResponse<Datasource> registerDatasource(@Valid @RequestBody DatasourceCreateRequestVO createDatasourceRequest) throws Exception;

    @Operation(summary = "修改", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @PutMapping("/data-sources/{id}")
    GenericResponse<Void> updateDatasource(
            @PathVariable(value = "id", required = false) final String id,
            @Valid @RequestBody DatasourceUpdateRequestVO datasourceUpdateRequest
    ) throws URISyntaxException;

    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "5", parseValue = true)})})
    @DeleteMapping("/data-sources/{id}")
    GenericResponse<Void> deleteDatasource(@PathVariable("id") String id);

    @Operation(summary = "验证", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @PostMapping("/data-sources/{id}/action/validate")
    GenericResponse<GenericResult> validateDatasource();
}