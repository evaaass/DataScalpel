package cn.superhuang.data.scalpel.admin.app.service.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.app.service.domain.ServiceEngine;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.admin.app.service.repository.ServiceEngineRepository;
import cn.superhuang.data.scalpel.admin.app.service.service.adapter.ServiceEngineAdapter;
import cn.superhuang.data.scalpel.model.common.TestResult;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineState;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceEngineService {

    @Resource
    private ServiceEngineRepository serviceEngineRepository;
    @Resource
    private RestServiceRepository serviceRepository;
    @Resource
    private List<ServiceEngineAdapter> engineAdapters;

    public TestResult testService(ServiceEngine serviceEngine) {
        //TODO 判断名字是否重名
        ServiceEngineAdapter engineAdapter = getEngineAdapter(serviceEngine.getType());
        return engineAdapter.test(serviceEngine.getProps());
    }

    public ServiceEngine registerEngine(ServiceEngine serviceEngine) {
        return testAndSaveServiceEngine(serviceEngine);
    }

    public void updateServiceEngine(ServiceEngine serviceEngine) {
        serviceEngineRepository.findById(serviceEngine.getId()).ifPresentOrElse(po -> {
            BeanUtil.copyProperties(serviceEngine, po, CopyOptions.create().ignoreNullValue());
            testAndSaveServiceEngine(po);
        }, () -> {
            throw new RuntimeException("引擎%s不存在".formatted(serviceEngine.getId()));
        });
    }

    public void unregisterServiceEngine(String id) {
        serviceEngineRepository.findById(id).ifPresent(engine -> {
            Long serviceCount = serviceRepository.countByEngineId(id);
            if (serviceCount > 0) {
                throw new RuntimeException("引擎下关联%s个服务，无法删除".formatted(serviceCount));
            }
            serviceEngineRepository.deleteById(id);
        });
    }

    private ServiceEngineAdapter getEngineAdapter(ServiceEngineType type) {
        for (ServiceEngineAdapter engineAdapter : engineAdapters) {
            if (engineAdapter.support(type))
                return engineAdapter;
        }
        throw new RuntimeException("不支持的服务引擎类型:" + type);
    }

    private ServiceEngine testAndSaveServiceEngine(ServiceEngine serviceEngine) {
        ServiceEngineAdapter engineAdapter = getEngineAdapter(serviceEngine.getType());
        TestResult testResult = engineAdapter.test(serviceEngine.getProps());
        if (testResult.getValid()) {
            serviceEngine.setState(ServiceEngineState.OK);
        } else {
            serviceEngine.setState(ServiceEngineState.ERROR);
            serviceEngine.setMsg(serviceEngine.getMsg());
        }
        return serviceEngineRepository.save(serviceEngine);
    }
}

