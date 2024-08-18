package cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration;

import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.model.task.node.NodeConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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

    @Schema(description = "采集项目列表")
    private List<JdbcInputItem> items;
}