package cn.superhuang.data.scalpel.admin.app.datafile.web.resource;

import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Tag(name = "30.数据文件管理", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "30", parseValue = true)})})
@RequestMapping("/api/v1")
public interface IDataFileResource {

    @Operation(summary = "查询", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "1", parseValue = true)})})
    @GetMapping("/data-files")
    @ResponseBody
    GenericResponse<Page<DataFile>> search(@ParameterObject GenericSearchRequestDTO searchRequest);

    @Operation(summary = "创建", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "2", parseValue = true)})})
    @PostMapping("/data-files")
    @ResponseBody
    GenericResponse<DataFile> createFile(@RequestParam(name = "alias", required = true) @Schema(description = "别名") String alias,
                                              @RequestParam(name = "catalogId") @Schema(description = "目录Id") String catalogId,
                                              @RequestParam(name = "type", required = false) @Schema(description = "类型") DataFileType type,
                                              @RequestParam(name = "description", required = false) @Schema(description = "描述") String description,
                                              @RequestParam(name = "options", required = false) @Schema(description = "高级参数") String options,
                                              @RequestPart(name = "file", required = false) MultipartFile file) throws JsonProcessingException;

    @Operation(summary = "修改", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "3", parseValue = true)})})
    @PostMapping("/data-files/{id}")
    @ResponseBody
    GenericResponse<Void> updateFile(@PathVariable("id") String id,
                                                  @RequestParam(name = "alias", required = false) @Schema(description = "别名") String alias,
                                                  @RequestParam(name = "catalogId") @Schema(description = "目录Id") String catalogId,
                                                  @RequestParam(name = "description", required = false) @Schema(description = "描述") String description,
                                                  @RequestParam(name = "options", required = false) @Schema(description = "高级参数") String options,
                                                  @RequestPart(name = "file", required = false) MultipartFile file) throws JsonProcessingException;


    @Operation(summary = "删除", extensions = {@Extension(properties = {@ExtensionProperty(name = "x-order", value = "4", parseValue = true)})})
    @DeleteMapping("/data-files/{id}")
    @ResponseBody
    GenericResponse<Void> delete(@PathVariable("id") String id);
}
