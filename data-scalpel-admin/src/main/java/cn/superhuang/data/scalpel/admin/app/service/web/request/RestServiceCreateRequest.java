package cn.superhuang.data.scalpel.admin.app.service.web.request;

import cn.superhuang.data.scalpel.admin.app.service.model.enumeration.RestServiceState;
import cn.superhuang.data.scalpel.admin.app.service.model.enumeration.RestServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class RestServiceCreateRequest {

    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "服务类型")
    private RestServiceType type;
    @Schema(description = "服务地址（相对）")
    private String uri;
    @Schema(description = "服务名称")
    private String name;
    @Schema(description = "服务描述")
    private String description;
    @Schema(description = "服务详细定义")
    private String serviceDefinition;
    
}
