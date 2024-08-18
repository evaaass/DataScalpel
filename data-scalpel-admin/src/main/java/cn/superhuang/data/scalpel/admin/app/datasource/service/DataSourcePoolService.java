package cn.superhuang.data.scalpel.admin.app.datasource.service;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DataSourcePoolService {

    public Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    //后面服务要考虑单独配置连接池数量的问题以支撑并发，这里只考虑用来系统管理使用的连接了
    public DataSource getDataSource(JdbcConfig jdbcConfig) throws Exception {
        String id = jdbcConfig.getUniqueId();
        if (!dataSourceMap.containsKey(id)) {
            SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
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
            HikariDataSource dataSource = new HikariDataSource(config);
            dataSourceMap.put(id, dataSource);
        }
        return dataSourceMap.get(id);
    }

    public Connection getConnection(JdbcConfig jdbcConfig) throws Exception {
        return getDataSource(jdbcConfig).getConnection();
    }

    public void cleanCache(JdbcConfig jdbcConfig) throws Exception {
        String id = jdbcConfig.getUniqueId();
        dataSourceMap.remove(id);
    }
}
