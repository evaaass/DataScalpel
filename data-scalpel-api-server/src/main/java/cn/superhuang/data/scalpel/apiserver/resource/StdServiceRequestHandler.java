package cn.superhuang.data.scalpel.apiserver.resource;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.model.ServiceMappingItem;
import cn.superhuang.data.scalpel.apiserver.model.StdQueryResult;
import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import cn.superhuang.data.scalpel.apiserver.service.DatasourceService;
import cn.superhuang.data.scalpel.apiserver.service.DynamicMappingService;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataArgs;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataResult;
import cn.superhuang.data.scalpel.lib.jdbc.util.JdbcDataQueryUtil;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.service.definition.SqlServiceDefinition;
import cn.superhuang.data.scalpel.model.service.definition.StdServiceDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
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
public class StdServiceRequestHandler extends BaseServiceRequestHandler {

    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DatasourceService datasourceService;

    @ResponseBody
    @Valid(requireLogin = false)
    public Object invoke(MagicHttpServletRequest request, MagicHttpServletResponse response,
                         @PathVariable(required = false) Map<String, Object> pathVariables,
                         @RequestHeader(required = false) Map<String, Object> defaultHeaders,
                         @RequestParam(required = false) Map<String, Object> parameters) throws Throwable {
        String mappingKey = Objects.toString(request.getMethod(), "GET").toUpperCase() + ":" + request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        ServiceMappingItem serviceMappingItem = dynamicMappingService.getMapping(mappingKey);
        Service service = serviceMappingItem.getService();

        String requestBody = IoUtil.readUtf8(request.getHttpInputMessage().getBody());
        if (StrUtil.isBlank(requestBody)) {
            requestBody = "{}";
        }
        JdbcQueryDataArgs queryDataArgs = objectMapper.readValue(requestBody, JdbcQueryDataArgs.class);

        Integer pageNo = queryDataArgs.getPageNo() == null ? 1 : queryDataArgs.getPageNo();
        Integer pageSize = getPageSize(queryDataArgs.getPageSize(), service.getConfig());
        queryDataArgs.setPageNo(pageNo);
        queryDataArgs.setPageSize(pageSize);


        DataSource dataSource = datasourceService.getJdbcDataSource(service.getDatasourceId());
        queryDataArgs.setJdbcConfig(datasourceService.getJdbcConfig(service.getDatasourceId()));
        if (service.getType() == ServiceType.SQL) {
            SqlServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), SqlServiceDefinition.class);
            queryDataArgs.setTableName(serviceDefinition.getSql());
            queryDataArgs.setSubQueryMode(true);
        } else {
            StdServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), StdServiceDefinition.class);
            queryDataArgs.setTableName(serviceDefinition.getTableName());
        }


        JdbcQueryDataResult result = JdbcDataQueryUtil.queryData(dataSource, queryDataArgs);
        StdQueryResult stdQueryResult = StdQueryResult.from(result);
        return stdQueryResult;
    }
}
