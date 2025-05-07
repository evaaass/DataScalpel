package cn.superhuang.data.scalpel.apiserver.resource;


import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.model.ServiceMappingItem;
import cn.superhuang.data.scalpel.apiserver.model.StdQueryResult;
import cn.superhuang.data.scalpel.apiserver.service.DatasourceService;
import cn.superhuang.data.scalpel.apiserver.service.DynamicMappingService;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataResult;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcSqlQueryDataArgs;
import cn.superhuang.data.scalpel.lib.jdbc.util.JdbcDataQueryUtil;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.service.definition.SqlServiceDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;
import org.ssssssss.magicapi.core.annotation.Valid;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Service
public class SqlServiceRequestHandler extends BaseServiceRequestHandler {
    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private ObjectMapper objectMapper;
    //    @Resource
//    private DsService dsService;
    @Resource
    private StdServiceRequestHandler stdServiceRequestHandler;
    @Autowired
    private DatasourceService datasourceService;

    @ResponseBody
    @Valid(requireLogin = false)
    public Object invoke(MagicHttpServletRequest request, MagicHttpServletResponse response,
                         @PathVariable(required = false) Map<String, Object> pathVariables,
                         @RequestHeader(required = false) Map<String, Object> defaultHeaders,
                         @RequestParam(required = false) Map<String, Object> parameters) throws Throwable {
        String requestMethod = request.getMethod();
        String mappingKey = Objects.toString(request.getMethod(), "GET").toUpperCase() + ":" + request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        ServiceMappingItem serviceMappingItem = dynamicMappingService.getMapping(mappingKey);
        Service service = serviceMappingItem.getService();
        SqlServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), SqlServiceDefinition.class);

        Integer pageNo = parameters.containsKey("pageNo") ? Integer.parseInt(parameters.get("pageNo").toString()) : 1;

//        List<DmpModelDTO> models = serviceDefinition.getModelIds().stream().map(modelId -> dbService.getDmpModel(modelId)).collect(Collectors.toList());
        DataSource dataSource = datasourceService.getJdbcDataSource(service.getDatasourceId());
        JdbcConfig jdbcConfig = datasourceService.getJdbcConfig(service.getDatasourceId());

        JdbcSqlQueryDataArgs queryDataArgs = new JdbcSqlQueryDataArgs();
        queryDataArgs.setJdbcConfig(jdbcConfig);
        queryDataArgs.setPageNo(pageNo);
        Integer requestPageSize = parameters.containsKey("pageSize") ? Integer.parseInt(parameters.get("pageSize").toString()) : null;
        queryDataArgs.setPageSize(getPageSize(requestPageSize, service.getConfig()));
        queryDataArgs.setSql(serviceDefinition.getSql());
        queryDataArgs.setReturnCount(true);
        queryDataArgs.setReturnSchema(false);
        JdbcQueryDataResult result = JdbcDataQueryUtil.queryDataBySQL(dataSource, queryDataArgs);
        StdQueryResult stdQueryResult = StdQueryResult.from(result);
        return stdQueryResult;
    }
}