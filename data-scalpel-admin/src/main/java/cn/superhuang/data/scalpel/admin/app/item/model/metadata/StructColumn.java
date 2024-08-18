package cn.superhuang.data.scalpel.admin.app.item.model.metadata;

import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StructColumn {
    @Schema(description = "编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String cnName;
    @Schema(description = "字段类型")
    private ColumnType type;
    @Schema(description = "precision")
    private Integer precision;
    @Schema(description = "scale")
    private Integer scale;
}
