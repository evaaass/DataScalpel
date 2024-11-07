package cn.superhuang.data.scalpel.actuator.canvas.node.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.DataMaskingConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.UUIDConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.DataMaskingAction;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.UUIDAction;
import lombok.Data;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

@Data
public class DataMaskingProcessor extends CanvasNode {
    private DataMaskingConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        if (configuration == null || CollectionUtil.isEmpty(configuration.getActions())) {
            return inputData;
        }
        for (DataMaskingAction action : configuration.getActions()) {
            CanvasTable table = inputData.getTableMap().get(action.getTable());
            Dataset<Row> dataset = table.getDataset();
//TODO 完善。。尽量用map
//
//
//            Dataset<String> columnData = table.getDataset().select(action.getFieldName()).as(Encoders.STRING());
//
//            Dataset<String> processedData = columnData.map((MapFunction<String, String>) value -> {
//                return action.getRule().mask(value); // 调用处理函数
//            }, Encoders.STRING());
//
//            // 将处理后的数据与原始 DataFrame 合并或替换列
//            Dataset<Row> newDf = processedData.withColumn(action.getFieldName(), processedData);


            table.setDataset(dataset);
        }
        return inputData;
    }
}
