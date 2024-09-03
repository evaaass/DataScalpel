package cn.superhuang.data.scalpel.admin.app.datasource.web.resource;

import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DsItem;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DsItemPreviewResult;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourcePreviewItemRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceListItemRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceGetItemMetadataRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceValidateRequestVO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.GenericResult;
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
import java.util.List;

@Validated
@ApiSupport(order = 10)
@Tag(name = "10.数据源管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IDataSourceResource {
    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/data-sources")
    GenericResponse<Page<Datasource>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "详情", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @GetMapping("/data-sources/{id}")
    GenericResponse<Datasource> getDatasource(@PathVariable(value = "id") String id);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PostMapping("/data-sources")
    GenericResponse<Datasource> createDatasource(@Valid @RequestBody DatasourceCreateRequestVO createDatasourceRequest) throws Exception;

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
    @PostMapping("/data-sources/actions/validate")
    GenericResponse<GenericResult> validateConnection(@RequestBody DatasourceValidateRequestVO validateRequest);

    @Operation(summary = "查询数据源下面的items", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "20", parseValue = true)})})
    @PostMapping("/data-sources/{id}/actions/get-items")
    GenericResponse<List<DsItem>> getDatasourceItems(@PathVariable String id, @RequestBody DatasourceListItemRequestVO listItemRequest);

    @Operation(summary = "预览数据源下面item的数据", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "21", parseValue = true)})})
    @PostMapping("/data-sources/{id}/items/actions/preview")
    GenericResponse<DsItemPreviewResult> getDatasourceItemPreviewData(String id, DatasourcePreviewItemRequestVO previewItemRequestVO) ;

    @Operation(summary = "查询item的元数据", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "22", parseValue = true)})})
    @PostMapping("/data-sources/{id}/items/actions/get-metadata")
    GenericResponse<DataTable> getDatasourceItemMetadata(@PathVariable String id, @RequestBody DatasourceGetItemMetadataRequestVO getItemMetadataRequest);
}
