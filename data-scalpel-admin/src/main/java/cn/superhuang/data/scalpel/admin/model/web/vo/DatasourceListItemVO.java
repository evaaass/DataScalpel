package cn.superhuang.data.scalpel.admin.model.web.vo;

import cn.superhuang.data.scalpel.admin.app.datasource.model.enumeration.DatasourceCategory;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "数据源列表项")
@Data
public class DatasourceListItemVO {
    @Schema(description = "唯一标识")
    private String id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "管理员")
    private String manager;

    @Schema(description = "数据源分类:数据源就用DS，目前存储用APP就行")
    private DatasourceCategory category;

    @Schema(description = "类型")
    private DatasourceType type;

    @Schema(description = "完整类型")
    private String fullType;

    @Schema(description = "数据源连接参数")
    private Map<String, String> props;

}
