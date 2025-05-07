package cn.superhuang.data.scalpel.apiserver.service;

import cn.superhuang.data.scalpel.apiserver.domain.Datasource;
import cn.superhuang.data.scalpel.apiserver.domain.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DatasourceService {
    @Resource
    private MagicDynamicDataSource dynamicDataSource;
    public Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    private Map<String, Datasource> dmpDsMap = null;

    @Resource
    private DatasourceRepository datasourceRepository;

    public void init() {
        List<Datasource> datasourceList = datasourceRepository.findAll();
        dmpDsMap = datasourceList.stream().collect(Collectors.toMap(Datasource::getId, ds -> ds));
        for (Datasource datasource : datasourceList) {
            JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(DatasourceType.JDBC, datasource.getProps());
            if (jdbcConfig.getDbType() == DbType.TD_ENGINE_RS) {
                jdbcConfig.putOption("httpConnectTimeout", "60000");
                jdbcConfig.putOption("httpSocketTimeout", "60000");
                jdbcConfig.putOption("messageWaitTimeout", "120000");
            }
            if (!dataSourceMap.containsKey(datasource.getId())) {
                try {
                    HikariConfig config = getHikariConfig(jdbcConfig);
                    HikariDataSource dataSource = new HikariDataSource(config);
                    dataSourceMap.put(datasource.getId(), dataSource);
                    dynamicDataSource.add(datasource.getName(), dataSource);
                } catch (Exception e) {
                    log.error("注册数据源失败:" + e.getMessage(), e);
                }
            }
        }
    }

    public DataSource getJdbcDataSource(String id) {
        return dataSourceMap.get(id);
    }

    public JdbcConfig getJdbcConfig(String id) {
        Datasource dmpDatasource = dmpDsMap.get(id);
        return (JdbcConfig) DatasourceConfig.getConfig(DatasourceType.JDBC, dmpDatasource.getProps());
    }

    public void register(Datasource datasource) {
        datasourceRepository.save(datasource);
        //TODO 这里要维护标准服务和magic-api的连接池
    }

    public void update(Datasource datasource) {
        datasourceRepository.save(datasource);
        //TODO 这里要维护标准服务和magic-api的连接池

    }

    public void delete(String id) {
        //无需判断是否有服务在使用，如果删除了服务异常了，重新注册回来就可以
        datasourceRepository.deleteById(id);
        //TODO 这里要维护标准服务和magic-api的连接池
    }

    private HikariConfig getHikariConfig(JdbcConfig jdbcConfig) {
        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(jdbcDialect.getDriver());
        config.setJdbcUrl(jdbcDialect.buildUrl(jdbcConfig)); // 数据库URL
        config.setUsername(jdbcConfig.getUsername()); // 数据库用户名
        config.setPassword(jdbcConfig.getPassword()); // 数据库密码
        config.setMaximumPoolSize(2); // 最大连接数
        config.setMinimumIdle(1); // 最小空闲连接数
        config.setIdleTimeout(30000); // 空闲连接超时时间，单位是毫秒
        config.setMaxLifetime(600000); // 连接最大存活时间，单位是毫秒
        config.setConnectionTimeout(30000); // 连接超时时间，单位是毫秒
        config.setLeakDetectionThreshold(2000); // 泄漏检测时间，单位是毫秒
        config.setValidationTimeout(5000); // 5秒
        config.setSchema(jdbcDialect.getSchema(jdbcConfig));
        return config;
    }
}