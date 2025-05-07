package cn.superhuang.data.scalpel.apiserver.model;

import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataResult;
import lombok.Data;

import java.util.List;

@Data
public class StdQueryResult {
    private Integer pageNo;
    private Integer pageSize;
    private Long totalCount;
    private List resultList;

    public static StdQueryResult from(JdbcQueryDataResult jdbcQueryDataResult) {
        StdQueryResult result = new StdQueryResult();
        result.setPageNo(jdbcQueryDataResult.getPageNo());
        result.setPageSize(jdbcQueryDataResult.getPageSize());
        result.setTotalCount(jdbcQueryDataResult.getTotal());
        result.setResultList(jdbcQueryDataResult.getRecords());
        return result;
    }
}
