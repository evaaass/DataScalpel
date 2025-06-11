package cn.superhuang.data.scalpel.apiserver.service;

import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    @Resource
    private List<ServiceAdmin> serviceAdmins;


    public ServiceTestResult test(Service service, HttpServletRequest request, HttpServletResponse response) {
        return getServiceAdmin(service).test(service, request, response);
    }

    public void create(Service service) throws Exception {
        getServiceAdmin(service).up(service);
    }

    public void delete(Service service) {
        getServiceAdmin(service).down(service);
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
