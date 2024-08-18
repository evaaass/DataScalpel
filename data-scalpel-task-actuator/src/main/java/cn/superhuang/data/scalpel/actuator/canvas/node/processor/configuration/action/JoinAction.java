package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class JoinAction {
    @Schema(description = "join后的表名")
    private String newTable;
    @Schema(description = "左表")
    private String leftTable;
    @Schema(description = "右表")
    private String rightTable;
    @Schema(description = "join类型")
    private JoinType joinType;
    @Schema(description = "join条件")
    private List<JoinCondition> conditions;
}
