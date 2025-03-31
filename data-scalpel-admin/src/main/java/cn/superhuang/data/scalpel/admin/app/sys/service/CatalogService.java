package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Catalog;
import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.admin.app.sys.repository.CatalogRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    @Resource
    private CatalogRepository catalogRepository;

    public Catalog create(Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    public void delete(String id) {
        catalogRepository.findById(id).ifPresent(catalog -> {
            catalogRepository.delete(catalog);
        });
    }

    public void update(Catalog catalog) {
        catalogRepository.findById(catalog.getId()).ifPresent(po -> {
            BeanUtil.copyProperties(catalog, po, CopyOptions.create().ignoreNullValue());
            catalogRepository.save(po);
        });
    }

    public List<CatalogTreeNode> getCatalogTreeByType(String type) {
        List<Catalog> catalogs = catalogRepository.findAllByType(type);
        List<CatalogTreeNode> rootCatalogs = catalogs.stream().filter(catalog -> catalog.getParentId() == null).map(catalog -> BeanUtil.copyProperties(catalog, CatalogTreeNode.class)).collect(Collectors.toList());
        recursion(rootCatalogs, catalogs);
        return rootCatalogs;
    }

    private void recursion(List<CatalogTreeNode> parentCatalogs, List<Catalog> catalogs) {
        for (CatalogTreeNode parentCatalog : parentCatalogs) {
            List<CatalogTreeNode> childrenCatalog = catalogs.stream().filter(catalog -> catalog.getParentId() != null && catalog.getParentId().equals(parentCatalog.getId())).map(catalog -> BeanUtil.copyProperties(catalog, CatalogTreeNode.class)).collect(Collectors.toList());
            if (childrenCatalog.size() > 0) {
                parentCatalog.setChildren(childrenCatalog);
                parentCatalog.setLeaf(false);
                recursion(childrenCatalog, catalogs);
            } else {
                parentCatalog.setLeaf(true);
            }

        }
    }
}
