package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UUIDAction {
    @Schema(description = "需要改名的表")
    private String table;
    @Schema(description = "UUID字段名称")
    private String fieldName;
}
