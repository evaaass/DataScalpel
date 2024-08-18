package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

//无需注册给SPARK，我们系统用的
public class OracleSDEDialect extends OracleDialect implements SdeDialect, Serializable {
    @Serial
    private static final long serialVersionUID = 6196131149975270415L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.SDE_ORACLE;
    }

    @Override
    public String getSchema(JdbcConfig config) {
        //ORACLE SDE 的schema必须是sde...by superhuang
        return "SDE";
    }

    @Override
    public String getSdeBaseInfoQuery(String table) {
        String sql = """
                SELECT
                    a.F_TABLE_NAME AS "table_name",
                    a.F_GEOMETRY_COLUMN AS "spatial_column",
                    b.SRID AS "sr_id",
                    b.SR_NAME AS "sr_name",
                    b.DEFINITION AS "sr",
                    CASE
                        WHEN a.GEOMETRY_TYPE = '1' THEN'POINT'
                        WHEN a.GEOMETRY_TYPE = '7' THEN'MULTIPOINT'
                        WHEN a.GEOMETRY_TYPE = '9' THEN'MULTILINESTRING'
                        WHEN a.GEOMETRY_TYPE = '11' THEN'MULTIPOLYGON'
                    ELSE NULL
                    END AS geometry_type
                FROM
                    SDE.GEOMETRY_COLUMNS a
                    LEFT JOIN SDE.ST_GEOMETRY_COLUMNS c ON a.F_TABLE_NAME = c.TABLE_NAME
                        LEFT JOIN SDE.ST_SPATIAL_REFERENCES b ON c.SRID = b.SRID
                WHERE
                    a.F_TABLE_NAME='%s'
                """.formatted(table);
        return sql;
    }

    @Override
    public String getSdeWktQuery(JdbcConfig config, String table, List<String> columns, String spatialColumn) {
        String colSelectStr = columns.stream().map(column -> quoteIdentifier(column)).collect(Collectors.joining(","));
        colSelectStr = StrUtil.isBlank(colSelectStr) ? "" : (colSelectStr + ",");
        colSelectStr = colSelectStr + "st_astext(" + quoteIdentifier(spatialColumn) + ") as " + spatialColumn;
        return "select " + colSelectStr + " from " + getTableWithSchema(table, config);
    }
}
