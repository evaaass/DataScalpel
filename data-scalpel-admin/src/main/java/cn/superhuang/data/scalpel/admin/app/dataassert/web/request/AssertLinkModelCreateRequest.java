package cn.superhuang.data.scalpel.admin.app.dataassert.web.request;

import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AssertLinkModelCreateRequest {
    @Schema(description = "表名")
    private String name;
    @Schema(description = "中文名称")
    private String cnName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "数据源ID")
    private String datasourceId;
}
