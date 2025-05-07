package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

import cn.superhuang.data.scalpel.model.DataTableColumn;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class JdbcQueryDataResult {
    private List<DataTableColumn> columns;

    private Integer pageNo;
    private Integer pageSize;

    private Long total;

    private List<? extends Map<String, Object>> records;

    private Date startTime;
    private Date endTime;
}
