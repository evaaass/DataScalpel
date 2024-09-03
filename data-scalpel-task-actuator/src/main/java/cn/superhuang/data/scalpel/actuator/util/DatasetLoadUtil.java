package cn.superhuang.data.scalpel.actuator.util;


import cn.superhuang.data.scalpel.actuator.ActuatorContext;
import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.HashMap;
import java.util.Map;

public class DatasetLoadUtil {
    public static Dataset<Row> loadDataset(String modelId, ActuatorContext context) {

        ModelDTO model = context.getTaskConfiguration().getModelMap().get(modelId);
        return loadDataset(model.getDatasourceId(),model.getName(),context);
    }

    public static Dataset<Row> loadDataset(String datasourceId,String tableName, ActuatorContext context) {
        JdbcConfig jdbcConfig = (JdbcConfig) context.getTaskConfiguration().getDatasourceMap().get(datasourceId);
        SysJdbcDialect jdbcDialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        String tableNameWithSchema = jdbcDialect.getTableWithSchema(tableName, jdbcConfig);

        Map<String, String> options = new HashMap<>();
        options.put("driver", jdbcDialect.getDriver());
        options.put("url", jdbcDialect.buildUrl(jdbcConfig));
        options.put("dbtable", tableNameWithSchema);
        options.put("user", jdbcConfig.getUsername());
        options.put("password", jdbcConfig.getPassword());
        //从jdbc去读数据
        return context.getSparkSession().read().format("jdbc").options(options).load();
    }
}