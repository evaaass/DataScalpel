package cn.superhuang.data.scalpel.admin.config;

import jakarta.annotation.Resource;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
public class SpringQrtzScheduler {

    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean.getScheduler();
    }

//    @Bean
//    @QuartzDataSource
//    public DataSource quartzDataSource() {
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("org.postgresql.Driver");
//        dataSourceBuilder.url("jdbc:postgresql://10.0.0.5:5432/DataScalpel");
//        dataSourceBuilder.username("postgres");
//        dataSourceBuilder.password("Gistack@123");
//        return dataSourceBuilder.build();
//    }
}