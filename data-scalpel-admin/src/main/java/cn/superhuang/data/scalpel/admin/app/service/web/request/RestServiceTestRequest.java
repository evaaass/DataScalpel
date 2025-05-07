package cn.superhuang.data.scalpel.admin.app.service.web.request;

import cn.superhuang.data.scalpel.model.service.enumeration.RestServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RestServiceTestRequest {

    @Schema(description = "服务类型")
    private RestServiceType type;
    @Schema(description = "服务详细定义")
    private String serviceDefinition;
    
}
