package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScalpelAdapter implements ServiceEngineAdapter {
    @Override
    public Boolean support(ServiceEngineType type) {
        return type == ServiceEngineType.SCALPEL;
    }

    @Override
    public TestResult test(Map<String, String> props) {
        return TestResult.builder().valid(true).build();
    }

    @Override
    public void online() {

    }

    @Override
    public void offline() {

    }
}
