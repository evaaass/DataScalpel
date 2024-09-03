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

    @Schema(description = "数据源所属菜单")
    private String catalogId;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,63}$")
    @Schema(description = "英文唯一标识", example = "英文数字下划线only,全局唯一")
    private String name;

    @NotNull
    @Length(min = 1, max = 16)
    @Schema(description = "中文名", example = "全局唯一")
    private String alias;

    @NotNull
    @Schema(description = "数据源分类:数据源就用DATASOURCE，存储就用DATA_STORAGE")
    private DatasourceCategory category;

    @NotNull
    @Schema(description = "数据源类型")
    private DatasourceType type;

    @Schema(description = "数据源属性值")
    private Map<String, String> props;

}
