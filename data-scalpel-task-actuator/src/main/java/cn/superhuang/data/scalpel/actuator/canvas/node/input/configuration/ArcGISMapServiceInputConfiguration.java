package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ArcGISMapServiceInputConfiguration  extends NodeConfiguration implements IDatasourceConfiguration {
    @Schema(description = "数据源列表")
    private List<ArcGISMapServiceInputItem> items;

    @Override
    public Set<String> getDatasourceIds() {
        return items.stream().map(ArcGISMapServiceInputItem::getDatasourceId).collect(Collectors.toSet());
    }
}
