package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.datasource.service.DataSourcePoolService;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.model.common.jdbc.JdbcQueryDataArgs;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.definition.StdServiceDefinition;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.admin.app.service.service.DynamicMappingService;
import cn.superhuang.data.scalpel.admin.app.service.web.StdServiceRequestHandler;
import cn.superhuang.data.scalpel.admin.util.QueryDslUtil;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class RestServiceStdAdapter implements RestServiceAdapter, InitializingBean {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private StdServiceRequestHandler requestHandler;
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private ModelRepository modelRepository;
    @Resource
    private DataSourcePoolService poolService;
    @Autowired
    private RestServiceRepository restServiceRepository;

    @Override
    public Boolean support(RestServiceType type) {
        return type == RestServiceType.STD;
    }

    @Override
    public ServiceTestResult test(RestService service, HttpServletRequest request, HttpServletResponse response) {
        ServiceTestResult serviceTestResult = new ServiceTestResult();
        try {
            String sdContent = service.getServiceDefinition();
            StdServiceDefinition serviceDefinition = objectMapper.readValue(sdContent, StdServiceDefinition.class);
            Model model = modelRepository.getReferenceById(serviceDefinition.getModelId());
            Datasource dataSource = datasourceRepository.getReferenceById(model.getDatasourceId());
            JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(dataSource.getType(), dataSource.getProps());

            JdbcQueryDataArgs queryDataArgs = new JdbcQueryDataArgs();
            queryDataArgs.setJdbcConfig(jdbcConfig);
            queryDataArgs.setTableName(model.getName());
            queryDataArgs.setPageSize(10);
            queryDataArgs.setPageNo(0);

            DataSource ds = poolService.getDataSource(jdbcConfig);

            List<Map<String, Object>> data = QueryDslUtil.queryData(ds, queryDataArgs);
            serviceTestResult.setSuccess(true);
            serviceTestResult.setResponseBody(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            e.printStackTrace();
            serviceTestResult.setSuccess(false);
            serviceTestResult.setMsg(e.getMessage());
            serviceTestResult.setErrorDetail(ExceptionUtil.stacktraceToString(e));
        }
        return serviceTestResult;
    }

    @Override
    public void up(RestService service) throws Exception {
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths(service.getUri())
//                .methods(RequestMethod.POST)
//                .build();
//        Method method = StdServiceRequestHandler.class.getDeclaredMethod("invoke", MagicHttpServletRequest.class, MagicHttpServletResponse.class, Map.class, Map.class, Map.class);
        dynamicMappingService.register(service, requestHandler);
    }

    @Override
    public void down(RestService service) {
        dynamicMappingService.unregister(service);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动的时候要注册全部的服务
        List<RestService> services = restServiceRepository.findAllByType(RestServiceType.STD);
        for (RestService service : services) {
            System.out.println(objectMapper.writeValueAsString(service));
            up(service);
        }
    }
}