package cn.superhuang.data.scalpel.admin.app.datasource.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.BaseException;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.*;
import cn.superhuang.data.scalpel.admin.app.service.impl.adaptor.BaseDsAdaptor;
import cn.superhuang.data.scalpel.admin.model.dto.DatasourceAddDTO;
import cn.superhuang.data.scalpel.admin.model.dto.DatasourceUpdateDTO;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.model.enumeration.DatasourceState;

import cn.superhuang.data.scalpel.model.GenericResult;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service Implementation for managing {@link Datasource}.
 */
@Slf4j
@Service
@Transactional
public class DataSourceService {

    @Resource
    private DatasourceRepository datasourceRepository;

    @Resource
    private List<BaseDsAdaptor> dsAdaptors;

    public Datasource save(DatasourceAddDTO datasourceAddDTO) {
        Datasource datasource = BeanUtil.copyProperties(datasourceAddDTO, Datasource.class);
        datasource.setLastCheckTime(new Date());
        datasource.setState(DatasourceState.OK);

        if (datasource.getType() == DatasourceType.JDBC) {
            datasource.setFullType(datasource.getType().name() + "/" + datasource.getProps().get("type"));
        } else {
            datasource.setFullType(datasource.getType().name());
        }

        return datasourceRepository.save(datasource);
    }

    public void update(DatasourceUpdateDTO datasourceUpdate) {
        datasourceRepository.findById(datasourceUpdate.getId()).ifPresent(existingDatasource -> {
            BeanUtil.copyProperties(datasourceUpdate, existingDatasource, CopyOptions.create().ignoreNullValue());
            datasourceRepository.save(existingDatasource);
        });
    }

    public void delete(String id) {
        log.debug("Request to delete Datasource : {}", id);
        datasourceRepository.deleteById(id);
    }

    public GenericResult validate(DatasourceConfig datasourceConfig) {
        BaseDsAdaptor dsAdaptor = getDsAdaptor(datasourceConfig);
        DsCheckResult checkResult = dsAdaptor.check(datasourceConfig);
        return checkResult;
    }

    public List<DsItem> getDatasourceItems(String id) {
        List<DsItem> dsItems = new ArrayList<>();
        datasourceRepository.findById(id).ifPresent(datasource -> {
            DatasourceConfig datasourceConfig = DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
            BaseDsAdaptor dsAdaptor = getDsAdaptor(datasourceConfig);
            DsListItemArgs dsListItemArgs = new DsListItemArgs();
            dsItems.addAll(dsAdaptor.listItem(datasourceConfig, dsListItemArgs));
        });
        return dsItems;
    }

    public DsItemPreviewResult getDatasourcePreviewData(String id, String item) {
        return datasourceRepository.findById(id).map(datasource -> {
            DatasourceConfig datasourceConfig = DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
            BaseDsAdaptor dsAdaptor = getDsAdaptor(datasourceConfig);
            DsItemPreviewResult result = dsAdaptor.getItemPreviewData(datasourceConfig, new DsGetItemPreviewDataArgs(item));
            return result;
        }).get();
    }

    public DsItemMetadata getDatasourceItemMetadata(String id, String item) {
        return datasourceRepository.findById(id).map(datasource -> {
            DatasourceConfig datasourceConfig = DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
            BaseDsAdaptor dsAdaptor = getDsAdaptor(datasourceConfig);
            DsItemMetadata result = dsAdaptor.getItemMetadata(datasourceConfig, new DsGetItemMetadataArgs(item));
            return result;
        }).get();
    }

    public BaseDsAdaptor getDsAdaptor(DatasourceConfig datasourceConfig) {
        for (BaseDsAdaptor dsAdaptor : dsAdaptors) {
            if (dsAdaptor.canHandle(datasourceConfig)) {
                return dsAdaptor;
            }
        }
        throw new BaseException("不支持的数据源类型:" + datasourceConfig);
    }
}
