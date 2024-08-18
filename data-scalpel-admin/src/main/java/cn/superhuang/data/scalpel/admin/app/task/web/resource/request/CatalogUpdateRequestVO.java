package cn.superhuang.data.scalpel.admin.app.task.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建目录")
public class CatalogUpdateRequestVO {
    private String id;
    @Schema(description = "目录名称")
    private String name;
    @Schema(description = "父节点ID")
    private String parentId;
}
