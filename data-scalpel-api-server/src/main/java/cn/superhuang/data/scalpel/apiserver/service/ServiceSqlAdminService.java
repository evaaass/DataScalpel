package cn.superhuang.data.scalpel.apiserver.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.domain.repository.ServiceRepository;
import cn.superhuang.data.scalpel.apiserver.model.StdQueryResult;
import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import cn.superhuang.data.scalpel.apiserver.resource.SqlServiceRequestHandler;
import cn.superhuang.data.scalpel.apiserver.resource.StdServiceRequestHandler;
import cn.superhuang.data.scalpel.apiserver.util.DefinitionUtil;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataResult;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcSqlQueryDataArgs;
import cn.superhuang.data.scalpel.lib.jdbc.util.JdbcDataQueryUtil;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.definition.SqlServiceDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.ssssssss.magicapi.core.model.BaseDefinition;
import org.ssssssss.magicapi.core.model.DataType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ServiceSqlAdminService implements ServiceAdmin, InitializingBean {
    @Resource
    private DatasourceService dsService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private SqlServiceRequestHandler sqlRequestHandler;
    @Resource
    private StdServiceRequestHandler stdRequestHandler;
    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private ServiceRepository serviceRepository;

    @Override
    public Boolean support(ServiceType type) {
        return type == ServiceType.SQL;
    }

    @Override
    public ServiceTestResult test(Service service, HttpServletRequest request, HttpServletResponse response) {
        ServiceTestResult serviceTestResult = new ServiceTestResult();
        try {
            Map<String, Integer> pagerMap = new HashMap<>();
            pagerMap.put("pageNo", 1);
            pagerMap.put("pageSize", 10);
            String sdContent = service.getServiceDefinition();
            SqlServiceDefinition serviceDefinition = objectMapper.readValue(sdContent, SqlServiceDefinition.class);

//            List<DmpModelDTO> models = serviceDefinition.getModelIds().stream().map(modelId -> dbService.getDmpModel(modelId)).collect(Collectors.toList());

            DataSource dataSource = dsService.getJdbcDataSource(service.getDatasourceId());

            JdbcSqlQueryDataArgs queryDataArgs = new JdbcSqlQueryDataArgs();
            queryDataArgs.setJdbcConfig(dsService.getJdbcConfig(service.getDatasourceId()));
            queryDataArgs.setPageSize(pagerMap.get("pageSize"));
            queryDataArgs.setPageNo(pagerMap.get("pageNo"));
            queryDataArgs.setSql(serviceDefinition.getSql());
            queryDataArgs.setReturnCount(true);
            queryDataArgs.setReturnSchema(true);

            JdbcQueryDataResult queryDataResult = JdbcDataQueryUtil.queryDataBySQL(dataSource, queryDataArgs);
            StdQueryResult jxslStdQueryResult = StdQueryResult.from(queryDataResult);

            serviceTestResult.setSuccess(true);
            serviceTestResult.setRequestBody(objectMapper.writeValueAsString(pagerMap));
            serviceTestResult.setResponseBody(objectMapper.writeValueAsString(jxslStdQueryResult));
            serviceTestResult.setRequestBodyDefinition(objectMapper.writeValueAsString(getRequestDefinition()));
            serviceTestResult.setResponseBodyDefinition(objectMapper.writeValueAsString(getResponseDefinition(queryDataResult.getRecords())));
        } catch (Exception e) {
            e.printStackTrace();
            serviceTestResult.setSuccess(false);
            serviceTestResult.setMsg(e.getMessage());
            serviceTestResult.setErrorDetail(ExceptionUtil.stacktraceToString(e));
        }
        return serviceTestResult;
    }

    @Override
    public void up(Service service) throws NoSuchMethodException {
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths(service.getUri())
//                .methods(RequestMethod.POST)
//                .build();
//        Method method = SqlServiceRequestHandler.class.getDeclaredMethod("invoke", MagicHttpServletRequest.class, MagicHttpServletResponse.class, Map.class, Map.class, Map.class);
        Service stdService = BeanUtil.copyProperties(service, Service.class);
        stdService.setMethod(HttpMethod.POST.name());

        dynamicMappingService.register(service, sqlRequestHandler);
        dynamicMappingService.register(service, stdRequestHandler);
    }

    @Override
    public void down(Service service) {
        dynamicMappingService.unregister(service);
        service.setMethod(HttpMethod.POST.name());
        dynamicMappingService.unregister(service);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动的时候要注册全部的服务
        List<Service> serviceList = serviceRepository.findAllByType(ServiceType.SQL);
//        List<DmpServiceDTO> serviceList = dbService.getServices().stream().filter(s -> s.getType() == ServiceType.SQL).collect(Collectors.toList());
        for (Service service : serviceList) {
            System.out.println(objectMapper.writeValueAsString(service));
            up(service);
        }
    }


    private List<BaseDefinition> getRequestDefinition() {
        List<BaseDefinition> requestDefinitions = new ArrayList<>();
        requestDefinitions.add(DefinitionUtil.buildDefinition("pageNo", null, DataType.Integer, "起始页码，默认从1开始", false));
        requestDefinitions.add(DefinitionUtil.buildDefinition("pageSize", null, DataType.Integer, "分页值", false));
        return requestDefinitions;
    }

    private List<BaseDefinition> getResponseDefinition(List<? extends Map<String, Object>> data) {
        List<BaseDefinition> definitions = new ArrayList<>();
        definitions.add(DefinitionUtil.buildDefinition("pageNo", null, DataType.Integer, "起始页码，默认从1开始", false));
        definitions.add(DefinitionUtil.buildDefinition("pageSize", null, DataType.Integer, "分页值", false));
        definitions.add(DefinitionUtil.buildDefinition("totalCount", null, DataType.Long, "起始页码，默认从1开始", false));
        BaseDefinition resultDef = DefinitionUtil.buildDefinition("resultList", null, DataType.Array, "数据排序", false);
        resultDef.setChildren(new ArrayList<>());
        definitions.add(resultDef);
        if (data.size() > 0) {
            Map<String, Object> record = data.get(0);
            for (String key : record.keySet()) {
                Object value = record.get(key);
                DataType dataType = DefinitionUtil.classToMagicDataType(value);
                resultDef.getChildren().add(DefinitionUtil.buildDefinition(key, value == null ? null : value.toString(), dataType, "", false));
            }
        }
        return definitions;
    }
}