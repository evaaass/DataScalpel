package cn.superhuang.data.scalpel.admin.app.model.model;

import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ModelUpdateDTO {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "别名")
    private String alias;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "字段信息")
    private List<ModelFieldDTO> fields;
}
