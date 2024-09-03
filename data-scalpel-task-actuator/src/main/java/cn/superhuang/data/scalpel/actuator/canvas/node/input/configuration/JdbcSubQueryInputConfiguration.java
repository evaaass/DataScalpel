package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;


import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * @Author: SuperHuang
 * @Description:
 * @Date: 2021/6/9
 * @Version: 1.0
 */
@Data
public class JdbcSubQueryInputConfiguration extends NodeConfiguration implements IDatasourceConfiguration {
    @Schema(description = "表英文名称")
    private String name;

    @Schema(description = "表中文名称")
    private String alias;

    @Schema(description = "数据源/数据存储ID")
    private String datasourceId;

    @Schema(description = "SQL语句")
    private String sql;

    @Override
    public Set<String> getDatasourceIds() {
        return Collections.singleton(datasourceId);
    }
}