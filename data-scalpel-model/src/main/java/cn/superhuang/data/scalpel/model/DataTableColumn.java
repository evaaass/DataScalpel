package cn.superhuang.data.scalpel.model;

import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DataTableColumn {
    @Schema(description = "原类型")
    private String originType;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String alias;
    @Schema(description = "字段描述")
    private String remark;
    @Schema(description = "字段类型")
    private ColumnType type;
    @Schema(description = "长度")
    private Integer precision;
    @Schema(description = "精度")
    private Integer scale;

}
