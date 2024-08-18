package cn.superhuang.data.scalpel.model.task.configuration;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class CanvasTaskConfiguration extends TaskConfiguration implements Serializable {
    @Serial
    private static final long serialVersionUID = 6710349404186809686L;
    private String canvas;
}
