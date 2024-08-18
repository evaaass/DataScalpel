package cn.superhuang.data.scalpel.admin.web.resource;

import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.model.web.vo.ScriptDetailVO;
import cn.superhuang.data.scalpel.admin.model.web.vo.ScriptListItemVO;
import cn.superhuang.data.scalpel.admin.web.resource.request.ScriptCreateRequestVO;
import cn.superhuang.data.scalpel.admin.web.resource.request.ScriptUpdateRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "脚本管理")
@RequestMapping("/api/v1")
public interface IScriptResource {
    @Operation(summary = "获取脚本列表")
    @GetMapping("/scripts")
    @ResponseBody
    public GenericResponse<Page<ScriptListItemVO>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建脚本")
    @PostMapping("/scripts")
    public GenericResponse<ScriptDetailVO> createScript(ScriptCreateRequestVO createRequest) throws Exception;

    //    @Operation(summary = "获取脚本详情")
//    @GetMapping("/scripts/{id}")
//    public GenericResponse<TaskDetailVO> getTask(@PathVariable("id") String id);

    @Operation(summary = "更新脚本")
    @PostMapping("/scripts/{id}")
    public GenericResponse<ScriptDetailVO> updateScript(@PathVariable("id") String id, ScriptUpdateRequestVO updateRequest) throws Exception;

    @ResponseBody
    @Operation(summary = "删除脚本")
    @DeleteMapping("/scripts/{id}")
    public GenericResponse<Void> deleteScript(@PathVariable("id") String id) throws Exception;


}
