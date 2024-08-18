package cn.superhuang.data.scalpel.admin.app.task.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "创建目录")
public class CatalogCreateRequestVO {

    @Schema(description = "目录类别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String type;

    @Schema(description = "目录名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Length(min = 1, max = 16)
    @NotNull
    private String name;

    @Schema(description = "父节点ID")
    private String parentId;
}
