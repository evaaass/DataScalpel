package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.datasource.service.DataSourcePoolService;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.definition.SqlServiceDefinition;
import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import cn.superhuang.data.scalpel.admin.app.service.service.DynamicMappingService;
import cn.superhuang.data.scalpel.admin.app.service.web.SqlServiceRequestHandler;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.util.ScalaUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.jdbc.JdbcSQLQueryBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestServiceSqlAdapter implements RestServiceAdapter, InitializingBean {

    @Resource
    private RestServiceRepository restServiceRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private SqlServiceRequestHandler requestHandler;
    @Resource
    private DynamicMappingService dynamicMappingService;
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private ModelRepository modelRepository;
    @Resource
    private DataSourcePoolService poolService;
    @Override
    public Boolean support(RestServiceType type) {
        return type == RestServiceType.SQL;
    }

    @Override
    public ServiceTestResult test(RestService service, HttpServletRequest request, HttpServletResponse response) {
        ServiceTestResult serviceTestResult = new ServiceTestResult();
        try {
            String sdContent = service.getServiceDefinition();
            SqlServiceDefinition serviceDefinition = objectMapper.readValue(sdContent, SqlServiceDefinition.class);

            List<Model> models = serviceDefinition.getModelIds().stream().map(modelId -> modelRepository.getReferenceById(modelId)).collect(Collectors.toList());

            Datasource dataSource = datasourceRepository.getReferenceById(models.get(0).getDatasourceId());
            JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(dataSource.getType(), dataSource.getProps());
            DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());


            Map<String, String> jdbcOptionsMap = new HashMap<>();
            jdbcOptionsMap.put(JDBCOptions.JDBC_URL(), jdbcDialect.buildUrl(jdbcConfig));
            jdbcOptionsMap.put(JDBCOptions.JDBC_DRIVER_CLASS(), jdbcDialect.getDriver());
            jdbcOptionsMap.put(JDBCOptions.JDBC_PREPARE_QUERY(), "");
            jdbcOptionsMap.put(JDBCOptions.JDBC_TABLE_NAME(), "("+serviceDefinition.getSql()+") t");
            JDBCOptions jdbcOptions = new JDBCOptions(ScalaUtil.convertToScalaImmutableMap(jdbcOptionsMap));
            JdbcSQLQueryBuilder queryBuilder = jdbcDialect.getJdbcSQLQueryBuilder(jdbcOptions).withOffset(0).withLimit(100).withColumns(new String[]{"*"});
            String sql = queryBuilder.build();
            System.out.println(sql);
            DataSource ds = poolService.getDataSource(jdbcConfig);

            List<Entity> entities = DbUtil.use(ds).query(sql);
            serviceTestResult.setSuccess(true);
            serviceTestResult.setResponseBody(objectMapper.writeValueAsString(entities));
        } catch (Exception e) {
            e.printStackTrace();
            serviceTestResult.setSuccess(false);
            serviceTestResult.setMsg(e.getMessage());
            serviceTestResult.setErrorDetail(ExceptionUtil.stacktraceToString(e));
        }
        return serviceTestResult;
    }

    @Override
    public void up(RestService service) throws NoSuchMethodException {
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths(service.getUri())
//                .methods(RequestMethod.POST)
//                .build();
//        Method method = SqlServiceRequestHandler.class.getDeclaredMethod("invoke", MagicHttpServletRequest.class, MagicHttpServletResponse.class, Map.class, Map.class, Map.class);
        dynamicMappingService.register(service, requestHandler);
    }

    @Override
    public void down(RestService service) {
        dynamicMappingService.unregister(service);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动的时候要注册全部的服务
        List<RestService> serviceList = restServiceRepository.findAllByType(RestServiceType.SQL);
        for (RestService service : serviceList) {
            System.out.println(objectMapper.writeValueAsString(service));
            up(service);
        }
    }
}