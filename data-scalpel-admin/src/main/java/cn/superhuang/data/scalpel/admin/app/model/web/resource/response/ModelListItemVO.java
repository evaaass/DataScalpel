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

}
