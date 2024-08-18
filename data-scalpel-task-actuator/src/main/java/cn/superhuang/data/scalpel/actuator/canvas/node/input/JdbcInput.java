package cn.superhuang.data.scalpel.actuator.canvas.node.input;

import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.JdbcInputConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.JdbcInputItem;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.HashMap;
import java.util.Map;


@Data
public class JdbcInput extends CanvasNode {
    @Schema(description = "配置参数")
    private JdbcInputConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {
        JdbcConfig jdbcConfig = (JdbcConfig) getContext().getTaskConfiguration().getDatasourceMap().get(configuration.getDatasourceId());
        for (JdbcInputItem item : configuration.getItems()) {

            SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());

            String tableNameWithSchema = jdbcDialect.getTableWithSchema(item.getItem(), jdbcConfig);

            Map<String, String> options = new HashMap<>();
            options.put("driver", jdbcDialect.getDriver());
            options.put("url", jdbcDialect.buildUrl(jdbcConfig));
            options.put("dbtable", tableNameWithSchema);
            options.put("user", jdbcConfig.getUsername());
            options.put("password", jdbcConfig.getPassword());
            //从jdbc去读数据
            Dataset<Row> dataset = getContext().getSparkSession().read().format("jdbc").options(options).load();


            CanvasTable canvasTable = new CanvasTable();
            canvasTable.setDataset(dataset);
            canvasTable.setName(item.getItem());
            inputData.getTableMap().put(canvasTable.getName(),canvasTable);

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
