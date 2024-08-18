package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceValidateRequestVO {
    @Schema(description = "英文唯一标识", example = "唯一表示，英文only")
    private String code;
    @Schema(description = "中文名", example = "全局唯一")
    private String name;
    @Schema(description = "数据源类型")
    private DatasourceType type;
    @Schema(description = "数据源属性")
    private Map<String, String> props;
}
