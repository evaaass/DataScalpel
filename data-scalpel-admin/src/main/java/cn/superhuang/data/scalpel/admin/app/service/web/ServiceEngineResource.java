package cn.superhuang.data.scalpel.admin.app.service.web;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.domain.ServiceEngine;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.admin.app.service.repository.ServiceEngineRepository;
import cn.superhuang.data.scalpel.admin.app.service.service.RestServiceManagerService;
import cn.superhuang.data.scalpel.admin.app.service.service.ServiceEngineService;
import cn.superhuang.data.scalpel.admin.app.service.web.request.*;
import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.RestServiceDetail;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.util.GenericSearchUtil;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class ServiceEngineResource implements IServiceEngineResource {
    @Resource
    private ServiceEngineService engineService;
    @Resource
    private ServiceEngineRepository serviceEngineRepository;
    @Autowired
    private ServiceEngineService serviceEngineService;

    @Override
    public GenericResponse<Page<ServiceEngine>> search(GenericSearchRequestDTO searchRequest) {
        Specification<ServiceEngine> spec = GenericSearchUtil.resolveSpecification(searchRequest.getSearch(), ServiceEngine.class);
        PageRequest pageRequest = GenericSearchUtil.resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        return GenericResponse.ok(serviceEngineRepository.findAll(spec, pageRequest));
    }

    @Override
    public GenericResponse<ServiceEngine> create(ServiceEngineCreateRequest createRequest) throws Exception {
        ServiceEngine serviceEngine = BeanUtil.copyProperties(createRequest, ServiceEngine.class);
        return GenericResponse.ok(serviceEngineService.registerEngine(serviceEngine));
    }

    @Override
    public GenericResponse<Void> update(String id, ServiceEngineUpdateRequest updateRequest) throws Exception {
        ServiceEngine serviceEngine = BeanUtil.copyProperties(updateRequest, ServiceEngine.class);
        serviceEngine.setId(id);
        serviceEngineService.updateServiceEngine(serviceEngine);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(String id) throws Exception {
        serviceEngineService.unregisterServiceEngine(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<TestResult> test(ServiceEngineTestRequest testRequest) throws Exception {
        ServiceEngine serviceEngine = BeanUtil.copyProperties(testRequest, ServiceEngine.class);
        return GenericResponse.ok(serviceEngineService.testService(serviceEngine));
    }
}
