package cn.superhuang.data.scalpel.admin.app.dataassert.web.impl;

import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.IAssertResource;
import cn.superhuang.data.scalpel.admin.app.dataassert.service.AssertCatalogService;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertLinkModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertLinkModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AssertResource extends BaseResource implements IAssertResource {

    @Resource
    private AssertCatalogService assertCatalogService;

    @Override
    public GenericResponse<List<CatalogTreeNode>> getCatalog() {
//        List<CatalogTreeNode> catalogs = assertCatalogService.getLakeCatalogTree();
//        return GenericResponse.ok(catalogs);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<String> createFileAssert(String name, String cnName, String catalogId, EntityType type, String description, String options, MultipartFile file) {
        return GenericResponse.ok(StrUtil.uuid());
    }

    @Override
    public GenericResponse<Void> updateFileAssert(String id, String cnName, String catalogId, EntityType type, String description, String options, MultipartFile file) {
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<String> createModel(AssertModelCreateRequest assertModelCreateRequest) {
        return GenericResponse.ok(StrUtil.uuid());
    }

    @Override
    public GenericResponse<String> updateModel(String id, AssertModelUpdateRequest assertModelUpdateRequest) {
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<String> createLinkModel(AssertLinkModelCreateRequest assertLinkModelCreateRequest) {
        return GenericResponse.ok(StrUtil.uuid());
    }

    @Override
    public GenericResponse<Void> updateLinkModel(String id, AssertLinkModelUpdateRequest assertLinkModelUpdateRequest) {
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(String type, String id) {
        return GenericResponse.ok();
    }
}
