package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdbcQueryDataAggregator {
    private String column;

    @Schema(description = "desc (倒序) | asc (正序)")
    private JdbcQueryDataAggregatorFunction func;

    @Schema(description = "别名")
    private String alias;
}
