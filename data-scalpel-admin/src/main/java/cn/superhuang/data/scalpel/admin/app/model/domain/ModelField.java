package cn.superhuang.data.scalpel.admin.app.model.domain;

import cn.hutool.core.math.MathUtil;
import cn.superhuang.data.scalpel.admin.domain.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.Instant;

/**
 * A LakeItem.
 */
@Data
@Entity
@Table(name = "admin_model_field")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModelField extends AbstractAuditingEntity<String> implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Schema(description = "模型ID")
    private String modelId;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "别名")
    private String alias;
    @Schema(description = "字段描述")
    private String description;
    @Schema(description = "字段类型")
    private ColumnType type;
    @Schema(description = "长度")
    private Integer precision;
    @Schema(description = "精度")
    private Integer scale;
    @Schema(description = "是否不能为空")
    private Boolean nullable;
    @Schema(description = "是否主键")
    private Boolean primaryKey;
    @Schema(description = "是否分区字段")
    private Boolean partitionKey;

    @JsonIgnore
    public Boolean isTypeEqual(ModelField newField) {
        if (!type.equals(newField.getType())) {
            return false;
        }
        if (type == null && newField.getType() != null) {
            return false;
        } else if (type != null && newField.getType() == null) {
            return false;
        } else if (!type.equals(newField.getType())) {
            return false;
        }
        if (scale == null && newField.getScale() == null) {

        } else if (scale == null && newField.getScale() != null) {
            return false;
        } else if (scale != null && newField.getScale() == null) {
            return false;
        } else if (!scale.equals(newField.getScale())) {
            return false;
        }
        if (precision == null && newField.getPrecision() == null) {

        } else if (precision == null && newField.getPrecision() != null) {
            return false;
        } else if (precision != null && newField.getPrecision() == null) {
            return false;
        } else if (!precision.equals(newField.getPrecision())) {
            return false;
        }
        return true;

    }
}
