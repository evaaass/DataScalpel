package cn.superhuang.data.scalpel.apiserver.service;

import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.domain.repository.ServiceRepository;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@org.springframework.stereotype.Service
public class ServiceService {

    @Resource
    private List<ServiceAdmin> serviceAdmins;
    @Resource
    private ServiceRepository serviceRepository;

    public ServiceTestResult test(Service service, HttpServletRequest request, HttpServletResponse response) {
        return getServiceAdmin(service).test(service, request, response);
    }

    public void create(Service service) throws Exception {
        getServiceAdmin(service).up(service);
        serviceRepository.save(service);
    }

    private void delete(Service service) {
        getServiceAdmin(service).down(service);
        serviceRepository.delete(service);
    }

    public void deleteById(String id) {
        serviceRepository.findById(id).ifPresent(service -> delete(service));
    }

    public void deleteByMethodAndUri(String method, String uri) {
        Service po = serviceRepository.findOneByMethodAndUri(method, uri);
        delete(po);
    }

    private ServiceAdmin getServiceAdmin(Service service) {
        for (ServiceAdmin serviceAdmin : serviceAdmins) {
            if (serviceAdmin.support(service.getType())) {
                return serviceAdmin;
            }
        }
        throw new RuntimeException("不支持的服务类型:" + service.getType());
    }

}
