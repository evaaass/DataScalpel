package cn.superhuang.data.scalpel.model.task.configuration;


import cn.superhuang.data.scalpel.model.task.definition.BatchCanvasTaskDefinition;
import cn.superhuang.data.scalpel.model.task.definition.TaskBaseDefinition;
import lombok.Data;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;


@Data
public class CanvasTaskConfiguration extends SparkTaskConfiguration implements Serializable {
    @Serial
    private static final long serialVersionUID = 6710349404186809686L;
    private String canvas;

    @Override
    public TaskBaseDefinition getTaskDefinition() {
        BatchCanvasTaskDefinition definition = new BatchCanvasTaskDefinition();
        definition.setCanvas(canvas);
        return definition;
    }
}
