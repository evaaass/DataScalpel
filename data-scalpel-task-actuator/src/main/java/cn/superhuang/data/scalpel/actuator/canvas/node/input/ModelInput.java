package cn.superhuang.data.scalpel.actuator.canvas.node.input;

import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.ModelInputConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.ModelInputItem;
import cn.superhuang.data.scalpel.actuator.util.DatasetLoadUtil;
import cn.superhuang.data.scalpel.actuator.util.DatasetTimeRangeUtil;
import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;


@Data
public class ModelInput extends CanvasNode {
    private ModelInputConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        for (ModelInputItem item : configuration.getItems()) {
            ModelDTO model = getContext().getTaskConfiguration().getModelMap().get(item.getModelId());
            Dataset<Row> dataset = DatasetLoadUtil.loadDataset(item.getModelId(), getContext());

            //TODO 后面这个时间过滤要不要改成上推呢
            dataset = DatasetTimeRangeUtil.filterByTimeRangeStrategy(dataset, item.getTimeFieldName(), getContext().getTaskConfiguration().getPlanTriggerTime(), getContext().getTaskConfiguration().getCycleType(), configuration.getStrategy());

            CanvasTable canvasTable = new CanvasTable();
            canvasTable.setDataset(dataset);
            canvasTable.setName(model.getName());
            canvasTable.setAlias(model.getAlias());
            inputData.getTableMap().put(canvasTable.getName(), canvasTable);

        }
        return inputData;
    }

//    @Override
//    public ConfigurationValidateResult validate(List<CanvasTable> inputTables) {
//        ConfigurationValidateResult validateResult = super.validate(inputTables);
//
////        if (configuration == null) {
////            validateResult.addError("$.configuration", "配置不能为空");
////            return validateResult;
////        }
//
//        String datasourceId = configuration.getDatasourceId();
//        if (StrUtil.isBlank(datasourceId)) {
//            validateResult.addError("$.configuration.datasourceId", "没有指定数据源");
//            return validateResult;
//        }
//
//
//        if (configuration.getItems() == null && configuration.getItems().size() == 0) {
//            validateResult.addError("$.configuration.items", "请选择要汇聚的表名");
//        } else {
//            configuration.getItems().forEach(item -> {
//                String name = item.getName();
//                String timeFieldName = item.getTimeFieldName();
//                if (StrUtil.isBlank(name)) {
//                    validateResult.addError("$.configuration.items", "表名不能为空");
//                }
//            });
//        }
//
//        CollectTimeStrategy timeStrategy = configuration.getCollectTimeStrategy();
//        CollectStrategyTypeEnum type = timeStrategy.getType();
//        if (timeStrategy == null || type == null) {
//            validateResult.addError("$.configuration.collectTimeStrategy.type", "采集时间类型不能为空");
//        } else if (type == CollectStrategyTypeEnum.TIME_RANGE) {
//            if (timeStrategy.getStartTime() == null) {
//                validateResult.addError("$.configuration.collectTimeStrategy.startTime", "采集起始时间不能为空");
//            }
//            if (timeStrategy.getEndTime() == null) {
//                validateResult.addError("$.configuration.collectTimeStrategy.endTime", "采集结束时间不能为空");
//            }
//        } else if (type == CollectStrategyTypeEnum.T1 && timeStrategy.getCycleType() == null) {
//            validateResult.addError("$.configuration.collectTimeStrategy.cycleType", "周期类型不能为空");
//        }
//
//        return validateResult;
//    }
}
