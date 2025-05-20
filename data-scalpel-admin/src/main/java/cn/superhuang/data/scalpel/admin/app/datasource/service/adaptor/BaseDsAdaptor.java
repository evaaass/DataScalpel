package cn.superhuang.data.scalpel.admin.app.datasource.service.adaptor;

import cn.superhuang.data.scalpel.admin.app.datasource.DsAdaptorException;
import cn.superhuang.data.scalpel.admin.app.datasource.dto.*;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;

import java.util.List;

public abstract class BaseDsAdaptor {

    public abstract Boolean canHandle(DatasourceConfig datasourceConfig);

    public abstract Boolean supportBatch();

    public abstract Boolean supportStream();

    public abstract Boolean supportCatalog();

    public abstract DsCheckResult check(DatasourceConfig datasourceConfig);

    public abstract List<DsItem> listItem(DatasourceConfig datasourceConfig, DsListItemArgs listItemArgs) throws DsAdaptorException;

    public abstract DsItemMetadata getItemMetadata(DatasourceConfig datasourceConfig, DsGetItemMetadataArgs getItemMetadataArgs) throws DsAdaptorException;

    public abstract DsItemPreviewResult getItemPreviewData(DatasourceConfig datasourceConfig, DsGetItemPreviewDataArgs getItemPreviewDataArgs) throws DsAdaptorException;
}
