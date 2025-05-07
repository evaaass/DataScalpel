package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"jdbcConfig"})
public class JdbcSqlQueryDataArgs {
    private JdbcConfig jdbcConfig;

    private String sql;

    private Integer pageSize;
    private Integer pageNo;

    private Boolean returnCount;
    private Boolean returnSchema;
}
