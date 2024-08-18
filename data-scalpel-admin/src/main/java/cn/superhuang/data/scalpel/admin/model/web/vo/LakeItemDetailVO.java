package cn.superhuang.data.scalpel.admin.model.web.vo;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Schema(description = "数据湖资源详情")
@Data
public class LakeItemDetailVO {
    @Schema(description = "唯一标识")
    private String id;

    @Schema(description = "目录ID")
    private String catalogId;

    @Schema(description = "目录完整URI")
    private String catalogUri;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型")
    private EntityType entityType;

    @Schema(description = "元数据类型")
    private String metadataType;

    @Schema(description = "元数据")
    private String metadata;

    @Schema(description = "创建时间")
    private Instant createTime;

    @Schema(description = "更新时间")
    private Instant modifyTime;
}
