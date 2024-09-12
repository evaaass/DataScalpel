package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.MD5Action;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.SQLAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SQLConfiguration {
    @Schema(description = "配置项")
    private List<SQLAction> actions;
}
