package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import lombok.Data;

@Data
public class CreateTempViewAction {
    public String table;
    public String tempViewName;
}
