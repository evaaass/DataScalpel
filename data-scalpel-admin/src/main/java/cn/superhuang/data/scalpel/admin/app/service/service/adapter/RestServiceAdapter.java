package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceTestResult;
import cn.superhuang.data.scalpel.admin.app.service.model.enumeration.RestServiceType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RestServiceAdapter {

    public Boolean support(RestServiceType type);

    public RestServiceTestResult test(RestService service, HttpServletRequest request, HttpServletResponse response);

    public void up(RestService service) throws NoSuchMethodException, Exception;

    public void down(RestService service);
}
