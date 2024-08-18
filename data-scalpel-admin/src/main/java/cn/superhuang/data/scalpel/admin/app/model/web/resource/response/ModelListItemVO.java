package cn.superhuang.data.scalpel.admin.app.model.web.resource.response;

import cn.superhuang.data.scalpel.admin.model.enumeration.ModelState;
import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.Date;

@Data
public class ModelListItemVO {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String cnName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "状态")
    private ModelState state;
    @Schema(description = "空间类型")
    @Enumerated(EnumType.STRING)
    private GeometryType geometryType;
    @Schema(description = "WKID")
    private Integer wkId;
    @Schema(description = "WK")
    private Integer wk;
    @Schema(description = "EXTENT")
    private String extent;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date modifyTime;
}
