package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DictTypeCreateRequestVO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "值")
    private String code;
    @Schema(description = "描述")
    private String description;
}
