package cn.superhuang.data.scalpel.admin.app.model.web.resource.response;

import cn.superhuang.data.scalpel.admin.model.enumeration.ModelState;
import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ModelDetailVO {
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


    @Schema(description = "字段信息")
    private List<ModelFieldDTO> fields;
    @Schema(description = "创建人ID")
    private String creatorId;
    @Schema(description = "创建人名称")
    private String creatorName;
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "目录URI")
    private String catalogUri;


}
