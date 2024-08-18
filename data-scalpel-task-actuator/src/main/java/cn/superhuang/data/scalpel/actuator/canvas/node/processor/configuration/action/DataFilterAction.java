package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DataFilterAction {
    @Schema(description = "数据流中的表")
    private String table;
    @Schema(description = "where条件，不需要以where开头")
    private String where;
}
