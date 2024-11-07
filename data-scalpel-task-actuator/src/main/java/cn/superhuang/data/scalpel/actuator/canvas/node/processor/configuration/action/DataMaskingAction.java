package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import cn.superhuang.data.scalpel.model.datamasking.DataMaskingRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMaskingAction {
    @Schema(description = "需要改名的表")
    private String table;
    @Schema(description = "UUID字段名称")
    private String fieldName;
    @Schema(description = "脱敏规则")
    private DataMaskingRule rule;
}
