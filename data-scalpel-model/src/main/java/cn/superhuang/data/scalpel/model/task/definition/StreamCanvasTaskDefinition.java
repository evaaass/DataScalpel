package cn.superhuang.data.scalpel.model.task.definition;

import lombok.Data;

@Data
public class StreamCanvasTaskDefinition extends SparkTaskDefinition {
    private String canvas;
}