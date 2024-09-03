package cn.superhuang.data.scalpel.admin.app.datasource.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DsItem;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DsItemMetadata;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DsItemPreviewResult;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.datasource.service.DataSourceService;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourcePreviewItemRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DatasourceAddDTO;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.DatasourceUpdateDTO;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceListItemRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceGetItemMetadataRequestVO;
import cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request.DatasourceValidateRequestVO;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.GenericResult;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Tag(name = "数据源管理")
@RestController
@RequestMapping("/api/v1")
public class DataSourceResource extends BaseResource implements IDataSourceResource {

	private final Logger log = LoggerFactory.getLogger(DataSourceResource.class);

	private static final String ENTITY_NAME = "datasource";
	@Resource
	private DataSourceService datasourceService;
	@Resource
	private DatasourceRepository datasourceRepository;

	public GenericResponse<Datasource> createDatasource(@RequestBody DatasourceCreateRequestVO createDatasourceRequest) throws Exception {
		DatasourceAddDTO datasourceDTO = BeanUtil.copyProperties(createDatasourceRequest, DatasourceAddDTO.class);
		return GenericResponse.ok(datasourceService.save(datasourceDTO));
	}

	public GenericResponse<Void> updateDatasource(
			@PathVariable(value = "id", required = false) final String id,
			@RequestBody DatasourceUpdateRequestVO datasourceUpdateRequest
	) throws URISyntaxException {
		DatasourceUpdateDTO datasourceUpdate = BeanUtil.copyProperties(datasourceUpdateRequest, DatasourceUpdateDTO.class);
		datasourceUpdate.setId(id);
		datasourceService.update(datasourceUpdate);
		return GenericResponse.ok();
	}

	public GenericResponse<Page<Datasource>> search(@ParameterObject GenericSearchRequestDTO searchRequest) {
		log.debug("REST request to get all Datasources");
		Specification<Datasource> spec = resolveSpecification(searchRequest.getSearch(), Datasource.class);
		PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
		Page<Datasource> page = datasourceRepository.findAll(spec, pageRequest);
		return GenericResponse.ok(page);
	}

	public GenericResponse<Datasource> getDatasource(@PathVariable String id) {
		log.debug("REST request to get Datasource : {}", id);
		Optional<Datasource> datasource = datasourceRepository.findById(id);
		return GenericResponse.wrapOrNotFound(datasource);
	}

	public GenericResponse<Void> deleteDatasource(@PathVariable String id) {
		log.debug("REST request to delete Datasource : {}", id);
		datasourceService.delete(id);
		return GenericResponse.ok();
	}

	@Override
	public GenericResponse<GenericResult> validateConnection(DatasourceValidateRequestVO validateRequest) {
		DatasourceConfig datasourceConfig = DatasourceConfig.getConfig(validateRequest.getType(), validateRequest.getProps());
		GenericResult result = datasourceService.validate(datasourceConfig);
		return GenericResponse.ok(result);
	}

	@Override
	public GenericResponse<List<DsItem>> getDatasourceItems(String id, DatasourceListItemRequestVO listItemRequest) {
		List<DsItem> result = datasourceService.getDatasourceItems(id);
		return GenericResponse.ok(result);
	}

	@Override
	public GenericResponse<DsItemPreviewResult> getDatasourceItemPreviewData(String id, DatasourcePreviewItemRequestVO previewItemRequestVO) {
		DsItemPreviewResult result = datasourceService.getDatasourcePreviewData(id, previewItemRequestVO.getItem());
		return GenericResponse.ok(result);
	}

	@Override
	public GenericResponse<DataTable> getDatasourceItemMetadata(String id, DatasourceGetItemMetadataRequestVO getItemMetadataRequest) {
		DsItemMetadata dsItemMetadata = datasourceService.getDatasourceItemMetadata(id, getItemMetadataRequest.getItem());
		return GenericResponse.ok(dsItemMetadata.getTable());
	}
}
