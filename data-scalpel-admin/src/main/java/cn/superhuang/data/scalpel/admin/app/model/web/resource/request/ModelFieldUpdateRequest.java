package cn.superhuang.data.scalpel.admin.app.model.web.resource.request;

import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ModelFieldUpdateRequest {
    @Schema(description = "字段信息")
    private List<ModelFieldDTO> fields;
}
