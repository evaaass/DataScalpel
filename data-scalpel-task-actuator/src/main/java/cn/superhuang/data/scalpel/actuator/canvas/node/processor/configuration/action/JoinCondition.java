package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JoinCondition {
    @Schema(description = "左表字段")
    private String leftTableField;
    @Schema(description = "右表字段")
    private String rightTableField;
}
