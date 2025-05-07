package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

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
public class JdbcQueryModelDataArgs {
    private Long modelId;

    private Integer pageSize;
    private Integer pageNo;
    /**
     * 传null或者为空，代表*，返回所有数据
     */
    private List<String> columns = new ArrayList<>();
    private List<JdbcQueryDataFilter> filters = new ArrayList<>();

}

