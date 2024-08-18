package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration;


import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.JoinAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinConfiguration extends NodeConfiguration {
    @Schema(description = "配置项")
    private List<JoinAction> actions;
}
