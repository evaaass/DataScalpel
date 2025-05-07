package cn.superhuang.data.scalpel.apiserver.service;


import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.model.ServiceMappingItem;
import cn.superhuang.data.scalpel.apiserver.resource.BaseServiceRequestHandler;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Service
public class DynamicMappingService {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private Map<String, ServiceMappingItem> mappingMap = new HashMap<>();


    public void register(Service service, BaseServiceRequestHandler requestHandler) throws NoSuchMethodException {
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(service.getUri())
                .methods(RequestMethod.valueOf(service.getMethod()))
                .build();
        Method method = requestHandler.getClass().getDeclaredMethod("invoke", MagicHttpServletRequest.class, MagicHttpServletResponse.class, Map.class, Map.class, Map.class);
        requestMappingHandlerMapping.registerMapping(requestMappingInfo, requestHandler, method);
        mappingMap.put(getKey(service), new ServiceMappingItem(requestMappingInfo, service));
    }

    public void unregister(Service service) {
        String key = getKey(service);
        if (!mappingMap.containsKey(key)) {
            return;
        }
        ServiceMappingItem serviceMappingItem = mappingMap.remove(key);
        requestMappingHandlerMapping.unregisterMapping(serviceMappingItem.getRequestMappingInfo());
    }

    public ServiceMappingItem getMapping(String key) {
        return mappingMap.get(key);
    }

    private String getKey(Service service) {
        return service.getMethod() + ":" + service.getUri();
    }

}
