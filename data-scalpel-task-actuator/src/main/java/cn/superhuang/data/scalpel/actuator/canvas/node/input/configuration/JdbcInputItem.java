package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdbcInputItem {
    @Schema(description = "表名")
    private String item;
    @Schema(description = "时间字段名")
    private String timeFieldName;
}
