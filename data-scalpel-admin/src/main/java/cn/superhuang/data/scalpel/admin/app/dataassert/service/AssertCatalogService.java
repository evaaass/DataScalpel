package cn.superhuang.data.scalpel.admin.app.dataassert.service;

import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.admin.app.sys.service.CatalogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssertCatalogService {

    @Resource
    private CatalogService catalogService;

//    public List<CatalogTreeNode> getLakeCatalogTree() {
//        CatalogTreeNode rootLakeNode = CatalogTreeNode.builder().name("数据湖").leaf(false).children(new ArrayList<>()).build();
//        addRAWNode(rootLakeNode);
//        addDWNode(rootLakeNode);
//        addAPPNode(rootLakeNode);
//        return List.of(rootLakeNode);
//    }

//    private void addRAWNode(CatalogTreeNode root) {
//        CatalogTreeNode dwNode = CatalogTreeNode.builder().name("原始数据层").leaf(true).build();
//        root.addChild(dwNode);
//        List<CatalogTreeNode> children = catalogService.getCatalogTreeByType(CatalogType.RAW);
//        if (children.size() > 0) {
//            dwNode.setChildren(children);
//            dwNode.setLeaf(true);
//        }
//    }
//
//    private void addDWNode(CatalogTreeNode root) {
//        CatalogTreeNode dwNode = CatalogTreeNode.builder().name("加工数据层").leaf(true).build();
//        root.addChild(dwNode);
//        List<CatalogTreeNode> children = catalogService.getCatalogTreeByType(CatalogType.DW);
//        if (children.size() > 0) {
//            dwNode.setChildren(children);
//            dwNode.setLeaf(true);
//        }
//    }
//
//    private void addAPPNode(CatalogTreeNode root) {
//        CatalogTreeNode dwNode = CatalogTreeNode.builder().name("应用数据层").leaf(true).build();
//        root.addChild(dwNode);
//        List<CatalogTreeNode> children = catalogService.getCatalogTreeByType(CatalogType.APP);
//        if (children.size() > 0) {
//            dwNode.setChildren(children);
//            dwNode.setLeaf(true);
//        }
//    }
}
