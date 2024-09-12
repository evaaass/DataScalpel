package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.ExprAction;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.SQLAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ExprConfiguration {
    @Schema(description = "配置项")
    private List<ExprAction> actions;
}