package cn.superhuang.data.scalpel.apiserver.service;

import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface ServiceAdmin {

    public Boolean support(ServiceType type);

    public ServiceTestResult test(Service service, HttpServletRequest request, HttpServletResponse response);

    public void up(Service service) throws Exception;

    public void down(Service service);
}
