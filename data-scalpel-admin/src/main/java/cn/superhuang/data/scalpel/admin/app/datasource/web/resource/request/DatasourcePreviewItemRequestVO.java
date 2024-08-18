package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourcePreviewItemRequestVO {
    @Schema(description = "一般都是表名")
    private String item;

}
