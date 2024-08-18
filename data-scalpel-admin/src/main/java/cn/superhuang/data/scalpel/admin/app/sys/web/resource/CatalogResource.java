package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.admin.app.sys.service.CatalogService;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.app.sys.repository.CatalogRepository;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.CatalogCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.CatalogUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Catalog;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CatalogResource extends BaseResource implements ICatalogResource {

    private final Logger log = LoggerFactory.getLogger(CatalogResource.class);

    @Resource
    private CatalogRepository catalogRepository;
    @Resource
    private CatalogService catalogService;

    @Override
    public GenericResponse<List<CatalogTreeNode>> getCatalogs(@NotNull String type, String parentId, Boolean tree) {
        if (tree) {
            List<CatalogTreeNode> catalogTreeNodes = catalogService.getCatalogTreeByType(type);
            return GenericResponse.ok(catalogTreeNodes);
        } else {
            List<Catalog> catalogs = catalogRepository.findAllByTypeAndParentId(type, parentId);
            List<CatalogTreeNode> result = catalogs.stream().map(catalog -> BeanUtil.copyProperties(catalog, CatalogTreeNode.class)).collect(Collectors.toList());
            return GenericResponse.ok(result);
        }
    }

    public GenericResponse<Catalog> createCatalog(CatalogCreateRequestVO catalogCreateRequest) throws Exception {
        Catalog catalog = catalogService.create(BeanUtil.copyProperties(catalogCreateRequest, Catalog.class));
        return GenericResponse.ok(catalog);
    }

    public GenericResponse<Void> updateCatalog(String id, CatalogUpdateRequestVO catalogUpdateRequest) {
        Catalog catalog = BeanUtil.copyProperties(catalogUpdateRequest, Catalog.class);
        catalog.setId(id);
        catalogService.update(catalog);
        return GenericResponse.ok();
    }

    public GenericResponse<Void> deleteCatalog(String id) {
        catalogService.delete(id);
        return GenericResponse.ok();
    }
}
