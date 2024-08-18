package cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
public class BaseOutputMapping implements Serializable {
    @Serial
    private static final long serialVersionUID = -8585169561351308527L;

    @Schema(title = "来源表ID")
    private String sourceTable;
    @Schema(title = "推送目标，如果是模型这里就是modelId")
    private String targetItem;
}