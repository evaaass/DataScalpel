package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.app.task.model.TimeRangeStrategy;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author: SuperHuang
 * @Description:
 * @Date: 2021/6/9
 * @Version: 1.0
 */
@Data
public class JdbcInputConfiguration extends NodeConfiguration implements IDatasourceConfiguration {
    @Schema(description = "数据源ID")
    private String datasourceId;
    @Schema(description = "采集策略")
    private TimeRangeStrategy strategy;
    @Schema(description = "采集项目列表")
    private List<JdbcInputItem> items;

    @Override
    public Set<String> getDatasourceIds() {
        return Set.of(datasourceId);
    }
}