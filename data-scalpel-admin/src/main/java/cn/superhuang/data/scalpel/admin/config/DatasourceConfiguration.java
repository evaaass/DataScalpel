package cn.superhuang.data.scalpel.admin.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfiguration {
    @Resource
    private DataSource dataSource;

    @Bean
    public MagicDynamicDataSource magicDynamicDataSource() {
        MagicDynamicDataSource dynamicDataSource = new MagicDynamicDataSource();
        dynamicDataSource.add("sys-db", dataSource);
        dynamicDataSource.setDefault(dataSource);
        return dynamicDataSource;
    }

}
