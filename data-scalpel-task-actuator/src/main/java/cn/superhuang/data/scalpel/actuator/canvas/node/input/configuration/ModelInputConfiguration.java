package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.IModelConfiguration;
import cn.superhuang.data.scalpel.app.task.model.TimeRangeStrategy;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: SuperHuang
 * @Description:
 * @Date: 2021/6/9
 * @Version: 1.0
 */
@Data
public class ModelInputConfiguration extends NodeConfiguration implements IModelConfiguration {

    @Schema(description = "采集策略")
    private TimeRangeStrategy strategy;

    @Schema(description = "采集模型列表")
    private List<ModelInputItem> items;

    @Override
    public Set<String> getModelIds() {
        return items.stream().map(item -> item.getModelId()).collect(Collectors.toSet());
    }
}