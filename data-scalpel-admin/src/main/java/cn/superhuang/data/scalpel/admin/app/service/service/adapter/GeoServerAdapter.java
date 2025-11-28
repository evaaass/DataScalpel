package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeoServerAdapter implements ServiceEngineAdapter {
    @Override
    public Boolean support(ServiceEngineType type) {
        return type == ServiceEngineType.GEOSERVER;
    }

    @Override
    public TestResult testServer(Map<String, String> props) {
        return TestResult.builder().valid(true).build();
    }

    @Override
    public TestResult testService(Map<String, String> props, String serviceDefinition) {
        return null;
    }

    @Override
    public void onlineService(Map<String, String> props, String serviceDefinition) {

    }

    @Override
    public void offlineService(Map<String, String> props, String serviceDefinition) {

    }


}
