package cn.superhuang.data.scalpel.admin.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.model.web.vo.LakeItemDetailVO;
import cn.superhuang.data.scalpel.admin.model.web.vo.LakeItemListItemVO;
import cn.superhuang.data.scalpel.admin.web.resource.request.LakeItemMetadataUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.web.resource.request.LakeItemUpdateRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Tag(name = "数据湖管理")
@RequestMapping("/api/v1")
public interface ILakeItemResource {

    @Operation(summary = "目录")
    @GetMapping("/lake/catalogs")
    @ResponseBody
    public GenericResponse<List<CatalogTreeNode>> getCatalog();

    @Operation(summary = "查询数据中的item")
    @GetMapping("/lake/items")
    @ResponseBody
    public GenericResponse<Page<LakeItemListItemVO>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "上传资源")
    @PostMapping("/lake/items/actions/upload")
    public GenericResponse<LakeItemDetailVO> uploadLakeItem(@Valid LakeItemUpdateRequestVO updateLakeItemRequest) throws Exception;

    @Operation(summary = "删除资源")
    @DeleteMapping("/lake/items/{id}")
    @ResponseBody
    public GenericResponse<Void> delete(@PathVariable String id) throws Exception;

    @Operation(summary = "更新元数据")
    @PutMapping("/lake/items/{id}/metadata")
    @ResponseBody
    public GenericResponse<Void> updateMetadata(@PathVariable String id, @RequestBody LakeItemMetadataUpdateRequestVO lakeItemMetadataUpdateRequestVO) throws Exception;
}
