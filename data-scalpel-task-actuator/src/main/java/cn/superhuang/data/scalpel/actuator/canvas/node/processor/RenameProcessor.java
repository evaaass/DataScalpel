package cn.superhuang.data.scalpel.actuator.canvas.node.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.DataFilterConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.RenameConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.DataFilterAction;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.RenameAction;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@Data
public class RenameProcessor extends CanvasNode {
    private RenameConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        if (configuration == null || CollectionUtil.isEmpty(configuration.getActions())) {
            return inputData;
        }
        for (RenameAction action : configuration.getActions()) {
            CanvasTable table = inputData.getTableMap().get(action.getTable());
            table.setName(action.getNewTableName());
            table.setCnName(action.getNewTableAlias());
        }
        return inputData;
    }
}