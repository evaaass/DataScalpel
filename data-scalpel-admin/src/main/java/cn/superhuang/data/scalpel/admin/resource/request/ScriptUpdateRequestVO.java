package cn.superhuang.data.scalpel.admin.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ScriptUpdateRequestVO {
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "脚本名称")
    private String name;
    @Schema(description = "脚本文件(文件和内容必填一个)")
    private MultipartFile contentFile;
    @Schema(description = "脚本内容(文件和内容必填一个)")
    private String content;
}