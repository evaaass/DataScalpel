package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;

import java.util.Map;

public interface ServiceEngineAdapter {

    public Boolean support(ServiceEngineType type);

    public TestResult test(Map<String, String> props);

    public void online();

    public void offline();
}
