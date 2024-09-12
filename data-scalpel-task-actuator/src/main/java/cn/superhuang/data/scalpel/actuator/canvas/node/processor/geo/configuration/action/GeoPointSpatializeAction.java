package cn.superhuang.data.scalpel.actuator.canvas.node.processor.geo.configuration.action;

import lombok.Data;

@Data
public class GeoPointSpatializeAction {
    private String table;
    private String lonFieldName;
    private String latFieldName;
    private String geoFieldName;
}
