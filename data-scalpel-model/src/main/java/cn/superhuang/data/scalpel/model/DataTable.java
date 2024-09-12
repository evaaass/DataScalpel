package cn.superhuang.data.scalpel.model;

import cn.superhuang.data.scalpel.model.enumeration.DataTableMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DataTable implements Serializable {
    @Serial
    private static final long serialVersionUID = -4759751568087804438L;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "中文名称")
    private String alias;
    @Schema(description = "字段信息")
    private List<DataTableColumn> columns;
    @Schema(description = "元数据信息")
    private Map<DataTableMetadata, Object> metadata = new HashMap<>();
}
