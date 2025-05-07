package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RestServiceAdapter {

    public Boolean support(RestServiceType type);

    public ServiceTestResult test(RestService service, HttpServletRequest request, HttpServletResponse response);

    public void up(RestService service) throws NoSuchMethodException, Exception;

    public void down(RestService service);
}
