package cn.superhuang.data.scalpel.admin.app.model.web.resource;

import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelFieldUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelDetailVO;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelListItemVO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "20.模型管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "20", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IModelResource {

    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/models")
    @ResponseBody
    GenericResponse<Page<Model>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "详情", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @GetMapping("/models/{id}")
    @ResponseBody
    GenericResponse<ModelDetailVO> detail(@PathVariable("id") String id);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PostMapping("/models")
    GenericResponse<Model> create(@RequestBody ModelCreateRequest createRequest) throws Exception;

    @Operation(summary = "更新", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @PutMapping("/models/{id}")
    GenericResponse<Void> update(@PathVariable("id") String id, @RequestBody ModelUpdateRequest updateRequest) throws Exception;

    @ResponseBody
    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "5", parseValue = true)})})
    @DeleteMapping("/models/{id}")
    public GenericResponse<Void> delete(@PathVariable("id") String id) throws Exception;


    @Operation(summary = "上线", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "10", parseValue = true)})})
    @PutMapping("/models/{id}/actions/online")
    GenericResponse<Void> online(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "下线", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "11", parseValue = true)})})
    @PutMapping("/models/{id}/actions/offline")
    GenericResponse<Void> offline(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "查询数据", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "13", parseValue = true)})})
    @GetMapping("/models/{id}/data")
    @ResponseBody
    GenericResponse<Page<Map<String, Object>>> searchModelData(@PathVariable("id") String id,
                                                               @ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "获取模型字段", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "20", parseValue = true)})})
    @GetMapping("/models/{id}/fields")
    GenericResponse<List<ModelField>> getFields(@PathVariable("id") String id) throws Exception;

    @Operation(summary = "更新模型字段", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "21", parseValue = true)})})
    @PutMapping("/models/{id}/fields")
    GenericResponse<Void> updateFields(@PathVariable("id") String id, @RequestBody ModelFieldUpdateRequest updateRequest) throws Exception;

    @Operation(summary = "强制重新创建物理表--超管内部用", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "30", parseValue = true)})})
    @PutMapping("/models/{id}/actions/recreate-table")
    GenericResponse<Void> forceRecreateTable(@PathVariable("id") String id) throws Exception;


}
