package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Map;


@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceUpdateRequestVO {
    @NotNull
    @Length(min = 1, max = 16)
    @Schema(description = "中文名", example = "全局唯一")
    private String name;

    @Schema(description = "数据源所属菜单")
    private String catalogId;

    @Schema(description = "管理员")
    private String manager;

    @Schema(description = "数据源属性值")
    private Map<String,String> props;
}
