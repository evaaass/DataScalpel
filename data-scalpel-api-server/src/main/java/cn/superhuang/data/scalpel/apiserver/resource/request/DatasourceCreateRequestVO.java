package cn.superhuang.data.scalpel.apiserver.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "采集任务执行参数参数")
public class DatasourceCreateRequestVO {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,63}$")
    @Schema(description = "英文唯一标识", example = "英文数字下划线only,全局唯一")
    private String name;

    @Schema(description = "数据源属性值")
    private Map<String, String> props;

}
