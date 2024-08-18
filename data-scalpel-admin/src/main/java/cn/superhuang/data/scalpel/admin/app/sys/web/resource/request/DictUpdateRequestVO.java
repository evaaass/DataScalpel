package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DictUpdateRequestVO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "值")
    private String value;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "顺序")
    private Integer index;
    @Schema(description = "上级ID")
    private String parentId;
    @Schema(description = "高级参数，KV形式，JSON转字符串")
    private String options;
}
