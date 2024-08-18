package cn.superhuang.data.scalpel.admin.app.datasource.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceGetItemMetadataRequestVO {

    private String item;
    @Schema(description = "扩展参数，暂不需要")
    private Map<String, String> options;

}
