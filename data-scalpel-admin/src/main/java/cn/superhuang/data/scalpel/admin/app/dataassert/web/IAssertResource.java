package cn.superhuang.data.scalpel.admin.app.dataassert.web;

import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertLinkModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertLinkModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.dataassert.web.request.AssertModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@Tag(name = "数据资产管理")
@RequestMapping("/api/v1")
public interface IAssertResource {

    @Operation(summary = "目录")
    @GetMapping("/data-asserts/catalogs")
    @ResponseBody
    public GenericResponse<List<CatalogTreeNode>> getCatalog();


    @Operation(summary = "上传资产")
    @PostMapping("/data-asserts/files/actions/upload-assert")
    @ResponseBody
    public GenericResponse<String> createFileAssert(@RequestParam(name = "name", required = true) @Schema(description = "名称") String name,
                                                    @RequestParam(name = "cnName", required = false) @Schema(description = "别名") String cnName,
                                                    @RequestParam(name = "catalogId") @Schema(description = "目录Id") String catalogId,
                                                    @RequestParam(name = "type", required = false) @Schema(description = "类型") EntityType type,
                                                    @RequestParam(name = "description", required = false) @Schema(description = "描述") String description,
                                                    @RequestParam(name = "options", required = false) @Schema(description = "高级参数") String options,
                                                    @RequestPart(name = "file", required = false) MultipartFile file);

    @Operation(summary = "更新资产")
    @PostMapping("/data-asserts/files/{id}")
    @ResponseBody
    public GenericResponse<Void> updateFileAssert(@PathVariable("id") String id,
                                                  @RequestParam(name = "cnName", required = false) @Schema(description = "别名") String cnName,
                                                  @RequestParam(name = "catalogId") @Schema(description = "目录Id") String catalogId,
                                                  @RequestParam(name = "type", required = false) @Schema(description = "类型") EntityType type,
                                                  @RequestParam(name = "description", required = false) @Schema(description = "描述") String description,
                                                  @RequestParam(name = "options", required = false) @Schema(description = "高级参数") String options,
                                                  @RequestPart(name = "file", required = false) MultipartFile file);

    @Operation(summary = "创建模型")
    @PostMapping("/data-asserts/model/actions/create-model")
    @ResponseBody
    public GenericResponse<String> createModel(@RequestBody AssertModelCreateRequest assertModelCreateRequest);

    @Operation(summary = "更新资产")
    @PutMapping("/data-asserts/model/{id}")
    @ResponseBody
    public GenericResponse<String> updateModel(@PathVariable("id") String id, @RequestBody AssertModelUpdateRequest assertModelUpdateRequest);

    @Operation(summary = "创建虚拟入湖模型")
    @PostMapping("/data-asserts/link-model/actions/create-model")
    @ResponseBody
    public GenericResponse<String> createLinkModel(@RequestBody AssertLinkModelCreateRequest assertLinkModelCreateRequest);

    @Operation(summary = "更新资产")
    @PutMapping("/data-asserts/link-model/{id}")
    @ResponseBody
    public GenericResponse<Void> updateLinkModel(@PathVariable("id") String id, @RequestBody AssertLinkModelUpdateRequest assertLinkModelUpdateRequest);


    @Operation(summary = "删除")
    @DeleteMapping("/data-asserts/{type}/{id}")
    @ResponseBody
    public GenericResponse<Void> delete(@PathVariable("type") String type, @PathVariable("id") String id);
}
