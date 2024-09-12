package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdbcInputItem {
    @Schema(description = "表名")
    private String item;
    @Schema(description = "时间字段名")
    private String timeFieldName;
    @Schema(description = "高级参数")
    private Map<String, String> options;
}
