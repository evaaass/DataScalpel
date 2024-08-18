package cn.superhuang.data.scalpel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GenericResult {
    @Schema(description = "是否正确")
    private Boolean success;
    @Schema(description = "异常消息")
    private String message;
    @Schema(description = "详情")
    private String detail;
}
