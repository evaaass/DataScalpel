package cn.superhuang.data.scalpel.actuator.canvas.node.input;

import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.JdbcSubQueryInputConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdbcSubQueryInput extends CanvasNode {
    private static final long serialVersionUID = 2102707349052238366L;
    @Schema(description = "配置参数")
    private JdbcSubQueryInputConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        return inputData;
    }
}
