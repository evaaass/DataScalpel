package cn.superhuang.data.scalpel.actuator.canvas.node.output;

import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration.FieldMapping;
import cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration.ModelOutputConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration.ModelOutputMapping;
import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DataSaveMode;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.util.SparkUtil;
import lombok.Data;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ModelOutput extends CanvasNode {
    private ModelOutputConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData inputData) {

        for (ModelOutputMapping outputMapping : configuration.getMappings()) {
            ModelDTO model = getContext().getTaskConfiguration().getModelMap().get(outputMapping.getTargetItem());
            JdbcConfig jdbcConfig = (JdbcConfig) getContext().getTaskConfiguration().getDatasourceMap().get(model.getDatasourceId());

            SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());

            CanvasTable table = inputData.getTableMap().get(outputMapping.getSourceTable());

            Dataset<Row> dataset = mappingFields(table.getDataset(), outputMapping.getFieldMappings());

            Map<String, String> options = jdbcDialect.getSparkExtraOptions();
            options.put("driver", jdbcDialect.getDriver());
            options.put("url", jdbcDialect.buildUrl(jdbcConfig));
            options.put("dbtable", jdbcDialect.getTableWithSchema(model.getName(), jdbcConfig));
            options.put("user", jdbcConfig.getUsername());
            options.put("password", jdbcConfig.getPassword());

            if (outputMapping.getSaveStrategy().getSaveMode() == DataSaveMode.OVERWRITE) {
                options.put("truncate", "true");
            }
            dataset.write().format("jdbc").mode(SaveMode.valueOf(outputMapping.getSaveStrategy().getSaveMode().getSparkValue()))
                    .options(options)
                    .save();
            table.setDataset(dataset);
        }
        return inputData;
    }

    public Dataset<Row> mappingFields(Dataset<Row> dataset, List<FieldMapping> fieldMappings) {
        List<Column> selectColumnList = new ArrayList<>();
        for (FieldMapping fieldMapping : fieldMappings) {
            if (StrUtil.isBlank(fieldMapping.getTargetFieldName()) || StrUtil.isBlank(fieldMapping.getSourceFieldName())) {
                //这种情况是前端传了无效参数
                continue;
            }
            Column column = dataset.col(SparkUtil.quoteIdentifier(fieldMapping.getSourceFieldName())).as(fieldMapping.getTargetFieldName());
            selectColumnList.add(column);
        }
        return dataset.select(selectColumnList.toArray(new Column[0]));
    }

}
