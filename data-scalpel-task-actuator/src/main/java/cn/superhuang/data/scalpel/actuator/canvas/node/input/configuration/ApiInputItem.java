package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ApiInputItem {
    @Schema(description = "数据源ID")
    private String datasourceId;
}
