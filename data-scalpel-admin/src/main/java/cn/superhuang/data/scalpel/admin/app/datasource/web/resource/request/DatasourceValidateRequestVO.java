package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceValidateRequestVO {
    @Schema(description = "数据源类型")
    private DatasourceType type;
    @Schema(description = "数据源属性")
    private Map<String, String> props;
}
