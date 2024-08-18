package cn.superhuang.data.scalpel.admin.app.service.model;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import lombok.Data;

import java.util.List;

@Data
public class JdbcQueryDataArgs {
    private JdbcConfig jdbcConfig;

    private String tableName;

    private Integer pageSize;
    private Integer pageNo;
    /**
     * 传null或者为空，代表*，返回所有数据
     */
    private List<String> columns;
    private List<JdbcQueryDataFilter> filters;
    private List<JdbcQueryDataOrder> orders;
}
