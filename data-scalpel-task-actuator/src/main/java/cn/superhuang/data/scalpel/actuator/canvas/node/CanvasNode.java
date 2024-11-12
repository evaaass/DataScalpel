package cn.superhuang.data.scalpel.actuator.canvas.node;

/**
 * @Author: SuperHuang
 * @Description:
 * @Date: 2021/6/7
 * @Version: 1.0
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cn.superhuang.data.scalpel.actuator.ActuatorContext;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.ValidateResult;
import cn.superhuang.data.scalpel.model.enumeration.CanvasNodeCategory;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonIgnoreProperties(value = {"context", "canvasData"})
public abstract class CanvasNode implements Serializable {
    @Serial
    private static final long serialVersionUID = 3839152422917817057L;

    @Schema(description = "节点ID，前端生成，画布中唯一即可")
    private String id;

    @Schema(description = "节点分类")
    private CanvasNodeCategory category;
//
//    @Schema(description = "节点具体类型")
//    private String type;

    @Schema(description = "前端用来记录在画布上位置的")
    private Integer x;
    @Schema(description = "前端用来记录在画布上位置的")
    private Integer y;

    private Boolean executed = false;

    private CanvasData canvasData;

    private ActuatorContext context;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CanvasNode that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract NodeConfiguration getConfiguration();

    public ValidateResult validate(List<DataTable> inputTables) {
        ValidateResult result = new ValidateResult();
        result.setNodeId(id);
        result.setValid(true);
        result.setMessages(new ArrayList<>());
        result.setErrors(new ArrayList<>());
        return result;
    }


    public abstract CanvasData execute(CanvasData inputData);

}