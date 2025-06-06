package cn.superhuang.data.scalpel.admin.app.service.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RestServiceUpdateRequest {

    private String id;
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "服务地址（相对）")
    private String uri;
    @Schema(description = "服务名称")
    private String name;
    @Schema(description = "服务描述")
    private String description;
    @Schema(description = "服务详细定义")
    private String serviceDefinition;
    
}
