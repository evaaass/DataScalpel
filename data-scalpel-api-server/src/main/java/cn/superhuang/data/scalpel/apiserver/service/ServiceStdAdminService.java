package cn.superhuang.data.scalpel.apiserver.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.domain.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.apiserver.domain.repository.ServiceRepository;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.model.StdQueryResult;
import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import cn.superhuang.data.scalpel.apiserver.resource.StdServiceRequestHandler;
import cn.superhuang.data.scalpel.apiserver.util.DefinitionUtil;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataArgs;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataResult;
import cn.superhuang.data.scalpel.lib.jdbc.util.JdbcDataQueryUtil;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.definition.StdServiceDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.ssssssss.magicapi.core.model.BaseDefinition;
import org.ssssssss.magicapi.core.model.DataType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceStdAdminService implements ServiceAdmin, InitializingBean {

    @Resource
    private DatasourceService dsService;
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private ObjectMapper objectMapper;


    @Resource
    private DynamicMappingService dynamicMappingService;

    @Resource
    private StdServiceRequestHandler requestHandler;

    @Resource
    private ServiceRepository serviceRepository;

    @Override
    public Boolean support(ServiceType type) {
        return type == ServiceType.STD;
    }

    @Override
    public ServiceTestResult test(Service service, HttpServletRequest request, HttpServletResponse response) {
        ServiceTestResult serviceTestResult = new ServiceTestResult();
        try {
            String sdContent = service.getServiceDefinition();
            StdServiceDefinition serviceDefinition = objectMapper.readValue(sdContent, StdServiceDefinition.class);

            DataSource dataSource = dsService.getJdbcDataSource(service.getDatasourceId());
            JdbcQueryDataArgs queryDataArgs = new JdbcQueryDataArgs();
            queryDataArgs.setJdbcConfig(dsService.getJdbcConfig(service.getDatasourceId().toString()));
            queryDataArgs.setTableName(serviceDefinition.getTableName());
            queryDataArgs.setPageSize(1);
            queryDataArgs.setPageNo(1);
            queryDataArgs.setReturnCount(false);
            JdbcQueryDataResult queryDataResult = JdbcDataQueryUtil.queryData(dataSource, queryDataArgs);
            StdQueryResult jxslStdQueryResult = StdQueryResult.from(queryDataResult);
            serviceTestResult.setSuccess(true);
            serviceTestResult.setRequestBody(objectMapper.writeValueAsString(new JdbcQueryDataArgs()));
            serviceTestResult.setResponseBody(objectMapper.writeValueAsString(jxslStdQueryResult));
            serviceTestResult.setRequestBodyDefinition(objectMapper.writeValueAsString(getRequestDefinition()));
            //serviceTestResult.setResponseBodyDefinition(objectMapper.writeValueAsString(getResponseDefinition(service.getModelFields())));
        } catch (Exception e) {
            e.printStackTrace();
            serviceTestResult.setSuccess(false);
            serviceTestResult.setMsg(e.getMessage());
            serviceTestResult.setErrorDetail(ExceptionUtil.stacktraceToString(e));
        }
        return serviceTestResult;
    }

    @Override
    public void up(Service service) throws Exception {
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths(service.getUri())
//                .methods(RequestMethod.POST)
//                .build();
//        Method method = StdServiceRequestHandler.class.getDeclaredMethod("invoke", MagicHttpServletRequest.class, MagicHttpServletResponse.class, Map.class, Map.class, Map.class);
        dynamicMappingService.register(service, requestHandler);
    }

    @Override
    public void down(Service service) {
        dynamicMappingService.unregister(service);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动的时候要注册全部的服务
        List<Service> stdServiceList = serviceRepository.findAllByType(ServiceType.STD);
//        List<DmpServiceDTO> stdServiceList = dbService.getServices().stream().filter(s -> s.getType() == ServiceType.STD).collect(Collectors.toList());
        for (Service service : stdServiceList) {
            System.out.println(objectMapper.writeValueAsString(service));
            up(service);
        }
    }

    private List<BaseDefinition> getRequestDefinition() {
        List<BaseDefinition> requestDefinitions = new ArrayList<>();
        requestDefinitions.add(DefinitionUtil.buildDefinition("pageNo", null, DataType.Integer, "起始页码，默认从1开始", false));
        requestDefinitions.add(DefinitionUtil.buildDefinition("pageSize", null, DataType.Integer, "分页值", false));
        requestDefinitions.add(DefinitionUtil.buildDefinition("columns", null, DataType.Array, "返回查询字段", false));

        requestDefinitions.add(DefinitionUtil.buildDefinition("groups", null, DataType.Array, "指定分组字段", false));


        BaseDefinition aggregatorsDef = DefinitionUtil.buildDefinition("aggregators", null, DataType.Array, "数据聚合", false);
        aggregatorsDef.setChildren(new ArrayList<>());
        aggregatorsDef.getChildren().add(DefinitionUtil.buildDefinition("aggregators.column", null, DataType.String, "指定聚合字段", false));
        aggregatorsDef.getChildren().add(DefinitionUtil.buildDefinition("aggregators.func", null, DataType.String, "聚合函数，count | sum | min | max | avg 等", false));
        requestDefinitions.add(aggregatorsDef);

        BaseDefinition ordersDef = DefinitionUtil.buildDefinition("orders", null, DataType.Array, "数据排序", false);
        ordersDef.setChildren(new ArrayList<>());
        ordersDef.getChildren().add(DefinitionUtil.buildDefinition("orders.column", null, DataType.String, "指定排序字段", false));
        ordersDef.getChildren().add(DefinitionUtil.buildDefinition("orders.direction", null, DataType.String, "指定排序方式，desc (倒序) | asc (正序)", false));
        requestDefinitions.add(ordersDef);

        BaseDefinition filtersDef = DefinitionUtil.buildDefinition("filters", null, DataType.Array, "过滤条件", false);
        filtersDef.setChildren(new ArrayList<>());
        filtersDef.getChildren().add(DefinitionUtil.buildDefinition("filters.name", null, DataType.String, "单条件过滤的字段名称", false));
        filtersDef.getChildren().add(DefinitionUtil.buildDefinition("filters.operator", null, DataType.String, "过滤条件操作符号：= | != | > | >= | < | <= | in | not in | between | like | not like 等", false));
        filtersDef.getChildren().add(DefinitionUtil.buildDefinition("filters.value", null, DataType.String, "过滤字段值列表", false));
        requestDefinitions.add(filtersDef);

        return requestDefinitions;
    }

    private List<BaseDefinition> getResponseDefinition(List<DataTableColumn> modelFields) {
        List<BaseDefinition> definitions = new ArrayList<>();
        definitions.add(DefinitionUtil.buildDefinition("pageNo", null, DataType.Integer, "起始页码，默认从1开始", false));
        definitions.add(DefinitionUtil.buildDefinition("pageSize", null, DataType.Integer, "分页值", false));
        definitions.add(DefinitionUtil.buildDefinition("totalCount", null, DataType.Long, "起始页码，默认从1开始", false));
        BaseDefinition resultDef = DefinitionUtil.buildDefinition("resultList", null, DataType.Array, "数据排序", false);
        resultDef.setChildren(new ArrayList<>());
        definitions.add(resultDef);
        if (!modelFields.isEmpty()) {
            for (DataTableColumn modelField : modelFields) {
                DataType dataType = DefinitionUtil.dmpTypeToMagicDataType(modelField.getType());
                StringBuilder desc = new StringBuilder(modelField.getAlias());
                if (StrUtil.isNotBlank(modelField.getRemark())) {
                    desc.append("|").append(modelField.getRemark());
                }
                resultDef.getChildren().add(DefinitionUtil.buildDefinition(modelField.getName(), null, dataType, desc.toString(), true));

            }
        }
        return definitions;
    }


}