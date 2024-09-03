package cn.superhuang.data.scalpel.admin.app.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class DatasourceUpdateDTO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "中文名", example = "全局唯一")
    private String alias;
    @Schema(description = "数据源所属菜单")
    private String catalogId;
    @Schema(description = "数据源属性值")
    private Map<String, String> props;
}
