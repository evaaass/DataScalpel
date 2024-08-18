package cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.IModelConfiguration;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ModelOutputConfiguration extends NodeConfiguration implements IModelConfiguration {
    private List<ModelOutputMapping> mappings;

    @Override
    public Set<String> getModelIds() {
        return mappings.stream().map(m -> m.getTargetItem()).collect(Collectors.toSet());
    }
}
