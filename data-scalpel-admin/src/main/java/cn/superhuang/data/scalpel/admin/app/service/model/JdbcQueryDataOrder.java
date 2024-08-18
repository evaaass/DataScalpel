package cn.superhuang.data.scalpel.admin.app.service.model;

import lombok.Data;

@Data
public class JdbcQueryDataOrder {
    private String column;
    /**
     * desc (倒序) | asc (正序)
     */
    private JdbcQueryDataOrderDirection direction;
}
