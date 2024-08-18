package cn.superhuang.data.scalpel.app.model.model;

import cn.superhuang.data.scalpel.model.enumeration.GeometryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ModelDTO {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String cnName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "目录ID")
    private String catalogId;
    @Schema(description = "标准规范ID")
    private String standardSpecificationId;
    @Schema(description = "字段信息")
    private List<ModelFieldDTO> fields;
    @Schema(description = "数据存储ID")
    private String datasourceId;
    @Schema(description = "要素类型")
    private GeometryType geometryType;
    @Schema(description = "坐标系ID")
    private Integer srId;
}
