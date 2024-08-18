package cn.superhuang.data.scalpel.admin.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Map;


@Data
@Schema(description = "更新元数据")
public class LakeItemMetadataUpdateRequestVO {
    @NotNull
    @Schema(description = "元数据内容")
    private String metadata;
}
