package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({"jdbcConfig", "tableName"})
public class JdbcQueryDataArgs {
    private JdbcConfig jdbcConfig;

    @Schema(description = "子查询模式")
    private Boolean subQueryMode;
    @Schema(description = "子查询模式为true是，这里是子查询SQL；为false时是表名")
    private String tableName;

    private Integer pageSize;
    private Integer pageNo;
    /**
     * 条件类型，or 任意条件触发，and 所有条件触发
     */
    private String conditionType;
    /**
     * 传null或者为空，代表*，返回所有数据
     */
    private List<String> columns = new ArrayList<>();
    private List<JdbcQueryDataFilter> filters = new ArrayList<>();
    private List<JdbcQueryDataOrder> orders = new ArrayList<>();
    private List<JdbcQueryDataAggregator> aggregators = new ArrayList<>();
    private List<String> groups = new ArrayList<>();

    @Schema(description = "是否返回总记录数（表记录数很大时请设置为false）")
    private Boolean returnCount = true;

    public void standardizing() {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        if (aggregators == null) {
            aggregators = new ArrayList<>();
        }
        if (orders == null) {
            orders = new ArrayList<>();
        }
        if (filters == null) {
            filters = new ArrayList<>();
        }
        if (columns == null) {
            columns = new ArrayList<>();
        }
    }
}
