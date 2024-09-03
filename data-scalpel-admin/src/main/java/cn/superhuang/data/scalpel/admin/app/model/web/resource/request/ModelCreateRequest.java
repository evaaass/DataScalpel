package cn.superhuang.data.scalpel.admin.app.model.web.resource.request;

import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ModelCreateRequest {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "别名")
    private String alias;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "字段信息")
    private List<ModelFieldDTO> fields;
    @Schema(description = "数据存储ID")
    private String datasourceId;


}
