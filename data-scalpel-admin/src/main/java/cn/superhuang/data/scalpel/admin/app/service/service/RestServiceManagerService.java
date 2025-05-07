package cn.superhuang.data.scalpel.admin.app.service.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.model.service.RestServiceDetail;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceState;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.util.GenericSearchUtil;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RestServiceManagerService {

    @Resource
    private RestServiceRepository serviceRepository;

    public RestService createService(RestService service) {
        return serviceRepository.save(service);
    }

    public void updateService(RestService service) {
        serviceRepository.findById(service.getId()).ifPresent(po -> {
            if (po.getState() == RestServiceState.ONLINE) {
                throw new RuntimeException("上线服务无法修改");
            }
            BeanUtil.copyProperties(service, po, CopyOptions.create().ignoreNullValue());
            serviceRepository.save(po);
        });
    }

    public void deleteService(String id) {
        serviceRepository.findById(id).ifPresent(po -> {
            if (po.getState() == RestServiceState.ONLINE) {
                throw new RuntimeException("上线服务无法删除");
            }
            serviceRepository.delete(po);
        });
    }

    public RestServiceDetail getServiceDetail(String id) {
        RestService restService = serviceRepository.getReferenceById(id);
        return BeanUtil.copyProperties(restService, RestServiceDetail.class);
    }

    public Page<RestService> searchServices(String search, String limit, String sort) {
        Specification<RestService> spec = GenericSearchUtil.resolveSpecification(search, RestService.class);
        PageRequest pageRequest = GenericSearchUtil.resolvePageRequest(limit, sort);
        return serviceRepository.findAll(spec, pageRequest);
    }

    public ServiceTestResult testService(String serviceDefinition) {
        ServiceTestResult result = new ServiceTestResult();
        result.setSuccess(true);
        return result;
    }

    public void onlineService(String id) {
        serviceRepository.findById(id).ifPresent(po -> {
            if (po.getState() == RestServiceState.OFFLINE) {
                //TODO 调用adapter去上线服务
            }
        });
    }

    public void offlineService(String id) {
        serviceRepository.findById(id).ifPresent(po -> {
            if (po.getState() == RestServiceState.ONLINE) {
                //TODO 调用adapter去下线服务
            }
        });
    }


}

