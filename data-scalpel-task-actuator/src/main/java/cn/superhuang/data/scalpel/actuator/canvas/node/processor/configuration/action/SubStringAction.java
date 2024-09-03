package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import lombok.Data;

@Data
public class SubStringAction {
    private String table;
    private String fieldName;
    private Integer position;
    private Integer length;
}
