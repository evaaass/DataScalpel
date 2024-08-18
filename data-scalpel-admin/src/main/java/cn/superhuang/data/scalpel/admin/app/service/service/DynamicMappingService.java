package cn.superhuang.data.scalpel.admin.app.service.service;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceMappingItem;
import cn.superhuang.data.scalpel.admin.app.service.web.BaseServiceRequestHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class DynamicMappingService {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private Map<String, RestServiceMappingItem> mappingMap = new HashMap<>();


    public void register(RestService service, BaseServiceRequestHandler requestHandler) throws NoSuchMethodException {
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(service.getUri())
                .methods(RequestMethod.valueOf(service.getMethod().name()))
                .build();
        Method method = requestHandler.getClass().getDeclaredMethod("invoke", MagicHttpServletRequest.class, MagicHttpServletResponse.class, Map.class, Map.class, Map.class);
        requestMappingHandlerMapping.registerMapping(requestMappingInfo, requestHandler, method);
        mappingMap.put(getKey(service), new RestServiceMappingItem(requestMappingInfo, service));
    }

    public void unregister(RestService service) {
        RestServiceMappingItem serviceMappingItem = mappingMap.remove(getKey(service));
        requestMappingHandlerMapping.unregisterMapping(serviceMappingItem.getRequestMappingInfo());
    }

    public RestServiceMappingItem getMapping(String key) {
        return mappingMap.get(key);
    }

    private String getKey(RestService service) {
        return service.getMethod() + ":" + service.getUri();
    }

}
