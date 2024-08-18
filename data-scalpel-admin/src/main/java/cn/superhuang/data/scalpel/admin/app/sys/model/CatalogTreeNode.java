package cn.superhuang.data.scalpel.admin.app.sys.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogTreeNode {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "类别")
    private String type;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "排序（暂不使用）")
    private Integer index;
    @Schema(description = "父节点ID")
    private String parentId;
    @Schema(description = "是否叶子节点")
    private Boolean leaf;
    @Schema(description = "子节点")
    private List<CatalogTreeNode> children;

    public void addChild(CatalogTreeNode node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(node);
    }
}
