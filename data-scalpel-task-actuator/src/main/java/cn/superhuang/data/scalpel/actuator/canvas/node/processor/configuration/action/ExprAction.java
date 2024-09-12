package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExprAction {
    @Schema(description = "表名")
    private String table;
    @Schema(description = "字段名（存在就覆盖，不存在就新增）")
    private String fieldName;
    @Schema(description = "表达式")
    private String expr;
}