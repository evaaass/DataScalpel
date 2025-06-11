package cn.superhuang.data.scalpel.apiserver.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
import cn.superhuang.data.scalpel.apiserver.domain.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.apiserver.resource.request.DatasourceCreateRequestVO;
import cn.superhuang.data.scalpel.apiserver.resource.request.DatasourceUpdateRequestVO;
import cn.superhuang.data.scalpel.apiserver.service.DatasourceService;
import cn.superhuang.data.scalpel.impl.BaseResource;
import cn.superhuang.data.scalpel.model.GenericResult;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class DatasourceResource extends BaseResource implements IDatasourceResource {

    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private DatasourceService datasourceService;

    @Override
    public GenericResponse<Page<Datasource>> search(GenericSearchRequestDTO searchRequest) {
        Specification<Datasource> spec = resolveSpecification(searchRequest.getSearch(), Datasource.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<Datasource> page = datasourceRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse<Datasource> registerDatasource(DatasourceCreateRequestVO createDatasourceRequest) throws Exception {
        Datasource datasource = BeanUtil.copyProperties(createDatasourceRequest, Datasource.class);
        datasourceService.register(datasource);
        return GenericResponse.ok(datasource);
    }

    @Override
    public GenericResponse<Void> updateDatasource( DatasourceUpdateRequestVO datasourceUpdateRequest) throws URISyntaxException {
        Datasource datasource = BeanUtil.copyProperties(datasourceUpdateRequest, Datasource.class);
        datasourceService.update(datasource);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> deleteDatasource(String id) {
        datasourceService.delete(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<GenericResult> validateDatasource() {
        return GenericResponse.ok();
    }
}
