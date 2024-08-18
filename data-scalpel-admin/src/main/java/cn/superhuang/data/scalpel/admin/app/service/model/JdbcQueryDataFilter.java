package cn.superhuang.data.scalpel.admin.app.service.model;

import lombok.Data;

import java.util.List;

@Data
public class JdbcQueryDataFilter {
    private String name;
    /**
     * = | != | > | >= | < | <= | in | not in | between | like | not like
     */
    private String operator;
    private List<Object> value;
}
