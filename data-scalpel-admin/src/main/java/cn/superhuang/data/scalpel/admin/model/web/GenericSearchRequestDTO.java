package cn.superhuang.data.scalpel.admin.model.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "通用查询参数")
@Data
public class GenericSearchRequestDTO {
    @Schema(description = "查询语法：name:*super*")
    private String search;
    @Schema(description = "分页参数：0,10")
    private String limit;
    @Schema(description = "排序参数：-createTime,updateTime")
    private String sort;
}
