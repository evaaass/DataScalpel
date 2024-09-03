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
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String alias;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "状态")
    private ModelState state;
    @Schema(description = "创建时间")
    private Date createdDate;
    @Schema(description = "修改时间")
    private Date lastModifiedDate;


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
