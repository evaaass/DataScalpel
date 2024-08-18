package cn.superhuang.data.scalpel.admin.app.service.web;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceDetail;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceTestResult;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.admin.app.service.service.RestServiceManagerService;
import cn.superhuang.data.scalpel.admin.app.service.web.request.RestServiceCreateRequest;
import cn.superhuang.data.scalpel.admin.app.service.web.request.RestServiceTestRequest;
import cn.superhuang.data.scalpel.admin.app.service.web.request.RestServiceUpdateRequest;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class RestServiceResource implements IRestServiceResource {

    @Resource
    private RestServiceManagerService restServiceManagerService;
    @Resource
    private RestServiceRepository restServiceRepository;


    @Override
    public GenericResponse<Page<RestService>> search(GenericSearchRequestDTO searchRequest) {
        Page<RestService> result = restServiceManagerService.searchServices(searchRequest.getSearch(), searchRequest.getLimit(), searchRequest.getSort());
        return GenericResponse.ok(result);
    }

    @Override
    public GenericResponse<RestService> create(RestServiceCreateRequest createRequest) throws Exception {
        RestService restService = BeanUtil.copyProperties(createRequest, RestService.class);
        restService = restServiceManagerService.createService(restService);
        return GenericResponse.ok(restService);
    }

    @Override
    public GenericResponse<Void> update(String id, RestServiceUpdateRequest updateRequest) throws URISyntaxException {
        RestService restService = BeanUtil.copyProperties(updateRequest, RestService.class);
        restService.setId(id);
        restServiceManagerService.updateService(restService);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<RestServiceDetail> getDetail(String id) {
        return GenericResponse.ok(restServiceManagerService.getServiceDetail(id));
    }

    @Override
    public GenericResponse<Void> delete(String id) {
        restServiceManagerService.deleteService(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<RestServiceTestResult> test(RestServiceTestRequest testRequest) {
        return GenericResponse.ok(restServiceManagerService.testService(testRequest.getServiceDefinition()));
    }

    @Override
    public GenericResponse<Void> online(String id) {
        restServiceManagerService.onlineService(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> offline(String id) {
        restServiceManagerService.offlineService(id);
        return GenericResponse.ok();
    }
}
