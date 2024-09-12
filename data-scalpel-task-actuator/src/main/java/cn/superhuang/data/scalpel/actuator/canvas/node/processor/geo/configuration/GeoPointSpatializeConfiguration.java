package cn.superhuang.data.scalpel.actuator.canvas.node.processor.geo.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.processor.geo.configuration.action.GeoPointSpatializeAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class GeoPointSpatializeConfiguration {
    @Schema(description = "配置项")
    private List<GeoPointSpatializeAction> actions;
}
