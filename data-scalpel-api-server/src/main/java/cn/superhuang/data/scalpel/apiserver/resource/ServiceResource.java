package cn.superhuang.data.scalpel.apiserver.resource;

import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
import cn.superhuang.data.scalpel.apiserver.domain.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.service.DatasourceService;
import cn.superhuang.data.scalpel.apiserver.service.ServiceService;
import cn.superhuang.data.scalpel.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ServiceResource extends BaseResource implements IServiceResource {

    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private DatasourceService datasourceService;

    @Resource
    private ServiceService serviceService;

    @Override
    public GenericResponse<Page<Datasource>> search(GenericSearchRequestDTO searchRequest) {
        Specification<Datasource> spec = resolveSpecification(searchRequest.getSearch(), Datasource.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<Datasource> page = datasourceRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse testService(ServiceDTO service, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public GenericResponse<Void> upService(ServiceDTO service) throws Exception {
        return null;
    }

    @Override
    public GenericResponse<Void> downService(ServiceDTO service) {
        return null;
    }
}
