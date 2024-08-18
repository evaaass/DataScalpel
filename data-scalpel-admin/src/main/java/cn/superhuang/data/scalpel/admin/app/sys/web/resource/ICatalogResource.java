package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.model.CatalogTreeNode;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.CatalogCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.CatalogUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Catalog;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Validated
@ApiSupport(order = 2)
@Tag(name = "3.系统管理-目录管理")
@RequestMapping("/api/v1")
public interface ICatalogResource {
    @Operation(summary = "获取目录")
    @GetMapping("/catalogs")
    public GenericResponse<List<CatalogTreeNode>> getCatalogs(
            @Schema(description = "目录类型") @RequestParam @NotNull String type,
            @Schema(description = "父节点Id") @RequestParam(required = false) String parentId,
            @Schema(description = "父节点Id") @RequestParam Boolean tree);


    @Operation(summary = "创建目录")
    @PostMapping("/catalogs")
    public GenericResponse<Catalog> createCatalog(@Valid @RequestBody CatalogCreateRequestVO catalogCreateRequest) throws Exception;

    @Operation(summary = "修改目录")
    @PutMapping("/catalogs/{id}")
    public GenericResponse<Void> updateCatalog(
            @PathVariable(value = "id", required = true) @NotNull final String id,
            @RequestBody CatalogUpdateRequestVO catalogUpdateRequest
    ) throws URISyntaxException;


    @Operation(summary = "删除目录")
    @DeleteMapping("/catalogs/{id}")
    public GenericResponse<Void> deleteCatalog(@PathVariable @NotNull String id);
}
