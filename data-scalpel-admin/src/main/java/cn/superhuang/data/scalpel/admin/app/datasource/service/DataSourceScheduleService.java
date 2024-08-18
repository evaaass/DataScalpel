package cn.superhuang.data.scalpel.admin.app.datasource.service;


import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Transactional
@Slf4j
@Service
public class DataSourceScheduleService {

    @Resource
    private DatasourceRepository datasourceRepository;

    @Resource
    private DataSourceService dataSourceService;

    @Transactional
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void checkDatasourceState() {
        log.info("开始检查数据源状态");
        try {
            List<Datasource> datasourceList = datasourceRepository.findAll();
            for (Datasource datasource : datasourceList) {
                checkDatasourceState(datasource);
            }
        } catch (Exception e) {
            log.error("检查数据源状态发生异常:" + e.getMessage(), e);
        }
        log.info("结束检查数据源状态");
    }

    @Transactional
    public void checkDatasourceState(Datasource ds) {
//        datasourceRepository.findById(ds.getId()).ifPresent(datasource -> {
//            DatasourceConfig datasourceConfig = DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
//            BaseDsAdaptor dsAdaptor = dataSourceService.getDsAdaptor(datasourceConfig);
//            DsCheckResult result = dsAdaptor.check(datasourceConfig);
//            DatasourceMetric datasourceMetric = DatasourceMetric.builder()
//                    .lastCheckTime(LocalDateTime.now(Clock.systemDefaultZone()))
//                    .state(result.getSuccess() == true ? DatasourceState.OK : DatasourceState.ERROR)
//                    .info(result.getMessage())
//                    .build();
//            datasource.setMetric(datasourceMetric);
//            datasourceRepository.save(datasource);
//        });
    }
}
