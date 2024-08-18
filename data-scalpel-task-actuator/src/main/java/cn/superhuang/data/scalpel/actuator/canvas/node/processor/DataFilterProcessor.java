package cn.superhuang.data.scalpel.actuator.canvas.node.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.DataFilterConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.DataFilterAction;
import cn.superhuang.data.scalpel.model.DataTable;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DataFilterProcessor extends CanvasNode {
    private DataFilterConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        if (configuration == null || CollectionUtil.isEmpty(configuration.getActions())) {
            return inputData;
        }
        for (DataFilterAction action : configuration.getActions()) {
            CanvasTable table = inputData.getTableMap().get(action.getTable());
            String where = action.getWhere() == null ? null : action.getWhere().trim();
            if (where != null && where.toUpperCase().startsWith("WHERE")) {
                where = where.substring(5);
            }
            if (StrUtil.isNotBlank(where)) {
                Dataset<Row> dataset = table.getDataset().where(where);
                table.setDataset(dataset);
            }
        }
        return inputData;
    }
}
