package cn.superhuang.data.scalpel.spark.core.dialect;

import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.DbType;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

//无需注册给SPARK，我们系统用的
public class PostGISSDEDialect extends PostgresDialect implements SdeDialect,Serializable {
    @Serial
    private static final long serialVersionUID = 6196131149975270415L;

    @Override
    public Boolean canHandle(DbType type) {
        return type == DbType.SDE_POSTGRESQL;
    }

    @Override
    public String getSchema(JdbcConfig config) {
        //PG SDE 的schema必须是sde...by superhuang
        return "sde";
    }

    @Override
    public String getSdeBaseInfoQuery(String table) {
        String sql = """
				SELECT
                	a."table_name" as table_name,
                	a.spatial_column AS spatial_column,
                	a.srid AS sr_id,
                	b.sr_name AS sr_name,
                	b.srtext AS srs,
					CASE
                		WHEN c.datasetsubtype2 = 1  THEN 'POINT'
                		WHEN c.datasetsubtype2 = 2  THEN 'MULTIPOINT'
                		WHEN c.datasetsubtype2 = 3  THEN 'MULTILINESTRING'
                		WHEN c.datasetsubtype2 = 4 THEN 'MULTIPOLYGON'
                		ELSE null
                	END AS geometry_type
                FROM
                	SDE.sde_layers a
                	LEFT JOIN "public".sde_spatial_references b ON a.srid = b.srid
					LEFT JOIN gdb_items c on concat(a.database_name,'.',a."schema",'.',a."table_name")=c."name"			
                WHERE
                	a."table_name" = '%s'
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
