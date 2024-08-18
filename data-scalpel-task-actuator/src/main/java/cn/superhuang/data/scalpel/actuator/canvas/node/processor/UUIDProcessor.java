package cn.superhuang.data.scalpel.actuator.canvas.node.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.UUIDConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.UUIDAction;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

@Data
public class UUIDProcessor extends CanvasNode {
    private UUIDConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        if (configuration == null || CollectionUtil.isEmpty(configuration.getActions())) {
            return inputData;
        }
        for (UUIDAction action : configuration.getActions()) {
            CanvasTable table = inputData.getTableMap().get(action.getTable());
            Dataset<Row> dataset = table.getDataset().withColumn(action.getFieldName(), functions.expr("uuid()"));
            table.setDataset(dataset);
        }
        return inputData;
    }
}
