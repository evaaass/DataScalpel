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
import cn.superhuang.data.scalpel.admin.model.web.vo.DatasourceListItemVO;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.GenericResult;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "10.数据源管理")
@RequestMapping("/api/v1")
public interface IDataSourceResource {

    @Operation(summary = "创建数据源")
    @PostMapping("/data-sources")
    GenericResponse<Datasource> createDatasource(@Valid @RequestBody DatasourceCreateRequestVO createDatasourceRequest) throws Exception;

    @Operation(summary = "修改数据源")
    @PutMapping("/data-sources/{id}")
    GenericResponse<Void> updateDatasource(
            @PathVariable(value = "id", required = false) final String id,
            @Valid @RequestBody DatasourceUpdateRequestVO datasourceUpdateRequest
    ) throws URISyntaxException;


    @Operation(summary = "查询数据源")
    @GetMapping("/data-sources")
    GenericResponse<Page<DatasourceListItemVO>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "获取数据源详情")
    @GetMapping("/data-sources/{id}")
    GenericResponse<Datasource> getDatasource(@PathVariable(value = "id") String id);

    @Operation(summary = "删除数据源")
    @DeleteMapping("/data-sources/{id}")
    GenericResponse<Void> deleteDatasource(@PathVariable("id") String id);


    @Operation(summary = "验证数据源信息")
    @PostMapping("/data-sources/actions/validate")
    GenericResponse<GenericResult> validateConnection(@RequestBody DatasourceValidateRequestVO validateRequest);

    @Operation(summary = "查询数据源下面的items")
    @PostMapping("/data-sources/{id}/actions/get-items")
    GenericResponse<List<DsItem>> getDatasourceItems(@PathVariable String id, @RequestBody DatasourceListItemRequestVO listItemRequest);

    @Operation(summary = "预览数据源下面item的数据")
    @PostMapping("/data-sources/{id}/items/actions/preview")
    GenericResponse<DsItemPreviewResult> getDatasourceItemPreviewData(String id, DatasourcePreviewItemRequestVO previewItemRequestVO) ;

    @Operation(summary = "查询item的元数据")
    @PostMapping("/data-sources/{id}/items/actions/get-metadata")
    GenericResponse<DataTable> getDatasourceItemMetadata(@PathVariable String id, @RequestBody DatasourceGetItemMetadataRequestVO getItemMetadataRequest);
}
