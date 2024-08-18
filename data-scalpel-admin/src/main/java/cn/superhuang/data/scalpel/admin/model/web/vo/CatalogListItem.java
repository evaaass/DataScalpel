package cn.superhuang.data.scalpel.admin.model.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "目录列表项")
public class CatalogListItem {
    private String id;
    @Schema(description = "目录名称")
    private String name;
    @Schema(description = "父节点ID")
    private String parentId;
    @Schema(description = "排序值")
    private Integer index;
}
