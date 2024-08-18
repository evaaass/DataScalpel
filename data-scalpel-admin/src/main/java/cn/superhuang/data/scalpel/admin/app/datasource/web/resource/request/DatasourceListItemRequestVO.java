package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import cn.superhuang.data.scalpel.admin.app.datasource.dto.DsItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceListItemRequestVO {
    @NotNull
    @Schema(description = "数据类型", example = "数据库类型都传JDBC_TABLE就行")
    private DsItemType type;

    @NotNull
    @Schema(description = "父Item", example = "文件类型需要传上级文件夹")
    private String parentItem;

    @Schema(description = "扩展参数，暂不需要")
    private Map<String, String> options;

}
