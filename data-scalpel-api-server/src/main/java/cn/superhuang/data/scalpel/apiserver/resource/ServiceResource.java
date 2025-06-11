package cn.superhuang.data.scalpel.apiserver.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.domain.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.apiserver.domain.repository.ServiceRepository;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.service.DatasourceService;
import cn.superhuang.data.scalpel.apiserver.service.ServiceService;
import cn.superhuang.data.scalpel.impl.BaseResource;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
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
    private ServiceRepository serviceRepository;


    @Resource
    private ServiceService serviceService;

    @Override
    public GenericResponse<Page<Service>> search(GenericSearchRequestDTO searchRequest) {
        Specification<Service> spec = resolveSpecification(searchRequest.getSearch(), Service.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<Service> page = serviceRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse<ServiceTestResult> testService(ServiceDTO service, HttpServletRequest request, HttpServletResponse response) {
        return GenericResponse.ok(serviceService.test(BeanUtil.copyProperties(service,Service.class), request, response));
    }

    @Override
    public GenericResponse<Void> upService(ServiceDTO service) throws Exception {
        serviceService.create(BeanUtil.copyProperties(service,Service.class));
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> downService(ServiceDTO service) {
        serviceService.delete(BeanUtil.copyProperties(service,Service.class));
        return GenericResponse.ok();
    }
}
