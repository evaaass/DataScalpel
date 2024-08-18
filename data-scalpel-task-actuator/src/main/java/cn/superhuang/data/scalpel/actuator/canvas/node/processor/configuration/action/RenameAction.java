package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RenameAction {
    @Schema(description = "需要改名的表")
    private String table;
    @Schema(description = "新名称")
    private String newTableName;
    @Schema(description = "新中文名称")
    private String newTableCnName;
}
