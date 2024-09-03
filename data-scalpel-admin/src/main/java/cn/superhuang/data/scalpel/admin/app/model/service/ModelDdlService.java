package cn.superhuang.data.scalpel.admin.app.model.service;

import cn.hutool.db.Db;
import cn.superhuang.data.scalpel.admin.app.datasource.service.DataSourcePoolService;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.admin.util.JdbcDdlUtil;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.SysJdbcDialects;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Service
public class ModelDdlService {

    @Resource
    private DataSourcePoolService datasourcePoolService;


    public void createTable(JdbcConfig jdbcConfig, Model model, List<ModelField> fields) throws Exception {
        SysJdbcDialect dialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        String[] sqlList = JdbcDdlUtil.getCreateTableSql(dialect, model, fields);
        Db.use(datasourcePoolService.getDataSource(jdbcConfig)).executeBatch(sqlList);
    }

    public void dropTable(JdbcConfig jdbcConfig, Model model) throws Exception {
        SysJdbcDialect dialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        String[] sqlList = JdbcDdlUtil.getDropTableSql(dialect, model);
        Db.use(datasourcePoolService.getDataSource(jdbcConfig)).executeBatch(sqlList);
    }

    public void updateTable(JdbcConfig jdbcConfig, Model model, List<ModelField> oldFields, List<ModelField> newFields)
            throws Exception {
        SysJdbcDialect dialect = SysJdbcDialects.get(jdbcConfig.getDbType());
        DataSource dataSource = datasourcePoolService.getDataSource(jdbcConfig);
        Integer dbMajorVersion;
        try (Connection connection = dataSource.getConnection()) {
            dbMajorVersion = connection.getMetaData().getDatabaseMajorVersion();
        }
        String[] sqlList = JdbcDdlUtil.getUpdateTableSql(dialect, dialect.getTableWithSchema(model.getName(), jdbcConfig), oldFields, newFields, dbMajorVersion);
        Db.use(dataSource).executeBatch(sqlList);
    }
}