package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class SQLServerSDEDialect extends SQLServerDialect implements SdeDialect, Serializable {
    @Serial
    private static final long serialVersionUID = 8831783637423728123L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.SDE_SQLSERVER;
    }

    @Override
    public String getSchema(JdbcConfig config) {
        //SQLSERVER SDE 的schema必须是sde...by superhuang
        return "sde";
    }

    @Override
    public String getSdeBaseInfoQuery(String table) {
        String sql = """
                SELECT
                	a.f_table_name AS table_name,
                	a.f_geometry_column AS spatial_column,
                	b.auth_srid AS sr_id,
                	b.auth_name AS sr_name,
                	b.srtext AS srs,
                	CASE
                		WHEN a.geometry_type = 1 THEN'POINT'\s
                		WHEN a.geometry_type = 7 THEN'MULTIPOINT'\s
                		WHEN a.geometry_type = 9 THEN'MULTILINESTRING'\s
                		WHEN a.geometry_type = 11 THEN'MULTIPOLYGON'\s
                	ELSE NULL\s
                	END AS geometry_type\s
                FROM
                	SDE.SDE_GEOMETRY_COLUMNS a
                	LEFT JOIN SDE.SDE_SPATIAL_REFERENCES b ON a.srid = b.srid
                WHERE
                	a.f_table_name = '%s'
                """.formatted(table);
        return sql;
    }

    @Override
    public String getSdeWktQuery(JdbcConfig config, String table, List<String> columns, String spatialColumn) {
        String colSelectStr = columns.stream().map(column -> quoteIdentifier(column)).collect(Collectors.joining(","));
        colSelectStr = StrUtil.isBlank(colSelectStr) ? "" : (colSelectStr + ",");
        colSelectStr = colSelectStr +  quoteIdentifier(spatialColumn) + ".AsTextZM() as " + spatialColumn;
        return "select " + colSelectStr + " from " + getTableWithSchema(table, config);
    }
}
