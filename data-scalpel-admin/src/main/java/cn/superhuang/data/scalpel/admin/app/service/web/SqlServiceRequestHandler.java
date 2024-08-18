package cn.superhuang.data.scalpel.admin.app.service.web;


import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.datasource.service.DataSourcePoolService;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceMappingItem;
import cn.superhuang.data.scalpel.admin.app.service.model.definition.SqlServiceDefinition;
import cn.superhuang.data.scalpel.admin.app.service.service.DynamicMappingService;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.util.ScalaUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.jdbc.JdbcSQLQueryBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SqlServiceRequestHandler implements BaseServiceRequestHandler {
    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private ObjectMapper objectMapper;
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
        SqlServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), SqlServiceDefinition.class);


        List<Model> models = serviceDefinition.getModelIds().stream().map(modelId -> modelRepository.getReferenceById(modelId)).collect(Collectors.toList());

        Datasource dataSource = datasourceRepository.getReferenceById(models.get(0).getDatasourceId());
        JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(dataSource.getType(), dataSource.getProps());
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());


        Map<String, String> jdbcOptionsMap = new HashMap<>();
        jdbcOptionsMap.put(JDBCOptions.JDBC_URL(), jdbcDialect.buildUrl(jdbcConfig));
        jdbcOptionsMap.put(JDBCOptions.JDBC_DRIVER_CLASS(), jdbcDialect.getDriver());
        jdbcOptionsMap.put(JDBCOptions.JDBC_PREPARE_QUERY(), "");
        jdbcOptionsMap.put(JDBCOptions.JDBC_TABLE_NAME(), "(" + serviceDefinition.getSql() + ") t");
        JDBCOptions jdbcOptions = new JDBCOptions(ScalaUtil.convertToScalaImmutableMap(jdbcOptionsMap));
        JdbcSQLQueryBuilder queryBuilder = jdbcDialect.getJdbcSQLQueryBuilder(jdbcOptions).withOffset(0).withLimit(100).withColumns(new String[]{"*"});
        String sql = queryBuilder.build();
        System.out.println(sql);

        DataSource ds = poolService.getDataSource(jdbcConfig);

        List<Entity> entities = DbUtil.use(ds).query(sql);

        return GenericResponse.ok(entities);
    }
}