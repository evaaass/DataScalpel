package cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Base64Action {
    @Schema(description = "需要改名的表")
    private String table;
    @Schema(description = "MD5字段名称")
    private String fieldName;
}