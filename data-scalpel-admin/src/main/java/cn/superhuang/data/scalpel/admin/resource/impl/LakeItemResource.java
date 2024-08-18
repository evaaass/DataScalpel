package cn.superhuang.data.scalpel.admin.resource.impl;

import cn.superhuang.data.scalpel.admin.app.dataassert.service.AssertCatalogService;
import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.model.web.vo.LakeItemDetailVO;
import cn.superhuang.data.scalpel.admin.model.web.vo.LakeItemListItemVO;
import cn.superhuang.data.scalpel.admin.repository.LakeItemRepository;
import cn.superhuang.data.scalpel.admin.web.resource.request.LakeItemMetadataUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.web.resource.request.LakeItemUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.service.LakeItemService;
import cn.superhuang.data.scalpel.admin.web.resource.ILakeItemResource;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class LakeItemResource extends BaseResource implements ILakeItemResource {
    @Resource
    private LakeItemService lakeItemService;
    @Resource
    private LakeItemRepository lakeItemRepository;


    @Resource
    private AssertCatalogService lakeCatalogService;

    @Override
    public GenericResponse<List<CatalogTreeNode>> getCatalog() {
//        List<CatalogTreeNode> catalogs = lakeCatalogService.getLakeCatalogTree();
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Page<LakeItemListItemVO>> search(GenericSearchRequestDTO searchRequest) {
//        Specification<LakeItem> spec = resolveSpecification(searchRequest.getSearch(), LakeItem.class);
//        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
//        Page<LakeItem> page = lakeItemRepository.findAll(spec, pageRequest);
//        List<LakeItemListItemVO> listVo = lakeItemMapper.toListVo(page.getContent());
//        Page<LakeItemListItemVO> result = new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<LakeItemDetailVO> uploadLakeItem(LakeItemUpdateRequestVO updateLakeItemRequest) throws Exception {
        return null;
    }

    @Override
    public GenericResponse<Void> delete(String id) throws Exception {
        return null;
    }

    @Override
    public GenericResponse<Void> updateMetadata(String id, LakeItemMetadataUpdateRequestVO lakeItemMetadataUpdateRequestVO) throws Exception {
        return null;
    }
}
