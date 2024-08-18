package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ModelInputItem {
    @Schema(description = "表名")
    private String modelId;
}
