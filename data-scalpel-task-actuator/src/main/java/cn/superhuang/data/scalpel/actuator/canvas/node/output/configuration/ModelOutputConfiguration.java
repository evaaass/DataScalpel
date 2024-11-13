package cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.IModelConfiguration;
import cn.superhuang.data.scalpel.model.task.DataSaveStrategy;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ModelOutputConfiguration extends NodeConfiguration implements IModelConfiguration {
    private List<ModelOutputMapping> mappings;
    @Schema(description = "保存策略")
    private DataSaveStrategy saveStrategy;

    @Override
    public Set<String> getModelIds() {
        return mappings.stream().map(m -> m.getTargetItem()).collect(Collectors.toSet());
    }
}
