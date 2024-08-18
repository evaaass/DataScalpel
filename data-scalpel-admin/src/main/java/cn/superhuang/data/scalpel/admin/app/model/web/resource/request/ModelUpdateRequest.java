package cn.superhuang.data.scalpel.admin.app.model.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ModelUpdateRequest {
    @Schema(description = "中文名称")
    private String cnName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "目录ID")
    private String catalogId;
}
