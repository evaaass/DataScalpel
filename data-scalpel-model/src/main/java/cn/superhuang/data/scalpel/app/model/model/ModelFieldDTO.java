package cn.superhuang.data.scalpel.app.model.model;

import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A LakeItem.
 */
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModelFieldDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7450377335567833117L;
    @Schema(description = "ID")
    private String id;
    @Schema(description = "模型ID")
    private String modelId;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String cnName;
    @Schema(description = "字段描述")
    private String remark;
    @Schema(description = "字段类型")
    private ColumnType type;
    @Schema(description = "长度")
    private Integer precision;
    @Schema(description = "精度")
    private Integer scale;
    @Schema(description = "是否不能为空")
    private Boolean nullable;
    @Schema(description = "是否主键")
    private Boolean primaryKey;
}
