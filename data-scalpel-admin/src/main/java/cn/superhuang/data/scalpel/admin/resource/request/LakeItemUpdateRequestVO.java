package cn.superhuang.data.scalpel.admin.web.resource.request;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LakeItemUpdateRequestVO {

    @NotNull
    @Schema(description = "目录ID")
    private String catalogId;

    @NotNull
    @Schema(description = "类型")
    private EntityType entityType;

    @NotNull
    @Schema(description = "文件")
    private MultipartFile contentFile;


    private String options;
}
