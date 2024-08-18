package cn.superhuang.data.scalpel.model.task;

import cn.superhuang.data.scalpel.model.enumeration.DataSaveMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class DataSaveStrategy {
    private DataSaveMode saveMode;

    @Schema(title = "高级参数")
    private Map<String, String> options;
}
