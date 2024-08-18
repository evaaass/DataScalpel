package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration;


import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.DataFilterAction;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.RenameAction;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: SuperHuang
 * @Description:
 * @Date: 2021/6/9
 * @Version: 1.0
 */
@Data
public class RenameConfiguration extends NodeConfiguration {
    @Schema(description = "配置项")
    private List<RenameAction> actions;
}
