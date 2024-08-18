package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import cn.superhuang.data.scalpel.admin.app.datasource.model.enumeration.DatasourceCategory;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Map;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceCreateRequestVO {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,63}$")
    @Schema(description = "英文唯一标识", example = "唯一表示，英文only")
    private String code;

    @NotNull
    @Length(min = 1, max = 16)
    @Schema(description = "中文名", example = "全局唯一")
    private String name;

    @NotNull
    @Schema(description = "数据源类型")
    private DatasourceType type;

    @Schema(description = "管理员")
    private String manager;

    @NotNull
    @Schema(description = "数据源分类:数据源就用DS，目前存储用APP就行")
    private DatasourceCategory category;

    @Schema(description = "数据源所属菜单")
    private String catalogId;

    @Schema(description = "数据源属性值")
    private Map<String, String> props;
}
