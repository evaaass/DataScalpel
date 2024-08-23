package cn.superhuang.data.scalpel.admin.app.datasource.dto;

import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.model.enumeration.DatasourceCategory;
import cn.superhuang.data.scalpel.model.enumeration.DatasourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * A DTO for the {@link Datasource} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class DatasourceAddDTO implements Serializable {

    private String id;
    @Schema(description = "别名")
    private String alias;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "分类")
    private DatasourceCategory category;
    @Schema(description = "类型")
    private DatasourceType type;

    @Schema(description = "完整类型")
    private String fullType;

    @Schema(description = "目录id")
    private String catalogId;
    @Schema(description = "连接信息")
    private Map<String, String> props;

}
