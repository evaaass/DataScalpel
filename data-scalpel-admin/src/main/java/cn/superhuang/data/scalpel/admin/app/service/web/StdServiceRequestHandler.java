package cn.superhuang.data.scalpel.admin.app.service.web;


import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.datasource.service.DataSourcePoolService;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.JdbcQueryDataArgs;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceMappingItem;
import cn.superhuang.data.scalpel.admin.app.service.model.definition.StdServiceDefinition;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.admin.app.service.service.DynamicMappingService;
import cn.superhuang.data.scalpel.admin.util.QueryDslUtil;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;
import org.ssssssss.magicapi.core.annotation.Valid;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StdServiceRequestHandler implements BaseServiceRequestHandler {

    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RestServiceRepository restServiceRepository;
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private ModelRepository modelRepository;
    @Resource
    private DataSourcePoolService poolService;

    @ResponseBody
    @Valid(requireLogin = false)
    public Object invoke(MagicHttpServletRequest request, MagicHttpServletResponse response,
                         @PathVariable(required = false) Map<String, Object> pathVariables,
                         @RequestHeader(required = false) Map<String, Object> defaultHeaders,
                         @RequestParam(required = false) Map<String, Object> parameters) throws Throwable {
        String mappingKey = Objects.toString(request.getMethod(), "GET").toUpperCase() + ":" + request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        RestServiceMappingItem serviceMappingItem = dynamicMappingService.getMapping(mappingKey);
        RestService service = serviceMappingItem.getService();
        JdbcQueryDataArgs queryDataArgs = objectMapper.readValue(request.getHttpInputMessage().getBody(), JdbcQueryDataArgs.class);

        String sdContent = service.getServiceDefinition();
        StdServiceDefinition serviceDefinition = objectMapper.readValue(sdContent, StdServiceDefinition.class);
        Model model = modelRepository.getReferenceById(serviceDefinition.getModelId());
        Datasource dataSource = datasourceRepository.getReferenceById(model.getDatasourceId());
        JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(dataSource.getType(), dataSource.getProps());
        queryDataArgs.setJdbcConfig(jdbcConfig);
        queryDataArgs.setTableName(model.getName());

        DataSource ds = poolService.getDataSource(jdbcConfig);
        List<Map<String, Object>> data = QueryDslUtil.queryData(ds, queryDataArgs);
        return GenericResponse.ok(data);
    }
}
