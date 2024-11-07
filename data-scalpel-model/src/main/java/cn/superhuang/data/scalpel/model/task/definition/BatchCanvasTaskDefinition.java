package cn.superhuang.data.scalpel.model.task.definition;

import lombok.Data;

@Data
public class BatchCanvasTaskDefinition extends SparkTaskDefinition {
    private String canvas;
}
