package cn.superhuang.data.scalpel.admin.web.resource;

import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.app.item.domain.MetadataType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "元数据类型管理")
@RequestMapping("/api/v1")
public interface IMetadataTypeResource {
    @Operation(summary = "获取元数据类型列表")
    @GetMapping("/metadata-types")
    @ResponseBody
    public GenericResponse<Page<MetadataType>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建元数据类型")
    @PostMapping("/metadata-types")
    public GenericResponse<MetadataType> create(@RequestBody MetadataType MetadataType) throws Exception;

    @Operation(summary = "更新元数据类型")
    @PutMapping("/metadata-types/{id}")
    public GenericResponse<MetadataType> update(@PathVariable("id") String id, @RequestBody MetadataType MetadataType) throws Exception;

    @ResponseBody
    @Operation(summary = "删除元数据类型")
    @DeleteMapping("/metadata-types/{id}")
    public GenericResponse<Void> delete(@PathVariable("id") String id) throws Exception;


}
