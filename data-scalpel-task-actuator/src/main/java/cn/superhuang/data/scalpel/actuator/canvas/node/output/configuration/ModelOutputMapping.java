package cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration;

import cn.superhuang.data.scalpel.model.task.DataSaveStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ModelOutputMapping extends BaseOutputMapping implements Serializable {
    @Serial
    private static final long serialVersionUID = -3255615956481101238L;

    @Schema(description = "字段映射；如果为NULL，则自动使用dataset，不允许size为0")
    private List<FieldMapping> fieldMappings;
    @Schema(description = "保存策略")
    private DataSaveStrategy saveStrategy;

}
