package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;

import java.util.Map;

public interface ServiceEngineAdapter {

    public Boolean support(ServiceEngineType type);

    public TestResult testServer(Map<String, String> props);

    public TestResult testService(Map<String, String> props,String serviceDefinition);

    public void onlineService(Map<String, String> props,String serviceDefinition);

    public void offlineService(Map<String, String> props,String serviceDefinition);
}
