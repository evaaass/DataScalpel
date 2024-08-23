package cn.superhuang.data.scalpel.admin.app.model.web.resource;

import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelFieldUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelDetailVO;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelListItemVO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "20.模型管理")
@RequestMapping("/api/v1")
public interface IModelResource {


    @Operation(summary = "获取模型列表")
    @GetMapping("/models")
    @ResponseBody
    GenericResponse<Page<ModelListItemVO>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "获取模型数据")
    @GetMapping("/models/{id}/data")
    @ResponseBody
    GenericResponse<Page<Map<String, Object>>> searchModelData(@PathVariable("id") String id,
                                                               @ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "获取模型详情")
    @GetMapping("/models/{id}")
    @ResponseBody
    GenericResponse<ModelDetailVO> detail(@PathVariable("id") String id);

    @Operation(summary = "创建模型")
    @PostMapping("/models")
    GenericResponse<ModelDetailVO> create(@RequestBody ModelCreateRequest createRequest) throws Exception;

    @Operation(summary = "更新模型")
    @PutMapping("/models/{id}")
    GenericResponse<Void> update(@PathVariable("id") String id, @RequestBody ModelUpdateRequest updateRequest) throws Exception;

    @ResponseBody
    @Operation(summary = "删除模型")
    @DeleteMapping("/models/{id}")
    public GenericResponse<Void> delete(@PathVariable("id") String id) throws Exception;


    @Operation(summary = "获取模型字段")
    @GetMapping("/models/{id}/fields")
    GenericResponse<List<ModelFieldDTO>> getFields(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "更新模型字段")
    @PutMapping("/models/{id}/fields")
    GenericResponse<Void> updateFields(@PathVariable("id") String id, @RequestBody ModelFieldUpdateRequest updateRequest) throws Exception;

    @Operation(summary = "创建物理表")
    @PutMapping("/models/{id}/actions/create-table")
    GenericResponse<Void> createTable(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "强制重新创建物理表")
    @PutMapping("/models/{id}/actions/recreate-table")
    GenericResponse<Void> forceRecreateTable(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "上线模型")
    @PutMapping("/models/{id}/actions/online")
    GenericResponse<Void> online(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "下线模型")
    @PutMapping("/models/{id}/actions/offline")
    GenericResponse<Void> offline(@PathVariable("id") String id) throws Exception;
}
