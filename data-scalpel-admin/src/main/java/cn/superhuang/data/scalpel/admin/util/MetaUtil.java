package cn.superhuang.data.scalpel.admin.util;

import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author SuperHuang
 * @date 2021/08/05 17:35
 **/
public class MetaUtil extends cn.hutool.db.meta.MetaUtil {
    public static Table getTableMeta(String catalog, String schema, Connection conn, String tableName) {
        final Table table = Table.create(tableName);
        try {
            table.setCatalog(catalog);
            table.setSchema(schema);

            final DatabaseMetaData metaData = conn.getMetaData();

            // 获得表元数据（表注释）
            try (ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{TableType.TABLE.value()})) {
                if (null != rs) {
                    if (rs.next()) {
                        table.setComment(rs.getString("REMARKS"));
                    }
                }
            }

            // 获得主键
            try (ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName)) {
                if (null != rs) {
                    while (rs.next()) {
                        table.addPk(rs.getString("COLUMN_NAME"));
                    }
                }
            }

            // 获得列
            try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
                if (null != rs) {
                    while (rs.next()) {
                        table.setColumn(Column.create(table, rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DbRuntimeException("Get columns error!", e);
        }

        return table;
    }

    public static Table getTableMeta(Connection conn, String tableName) {
        final String catalog = getCataLog(conn);
        final String schema = getSchema(conn);
        return getTableMeta(catalog, schema, conn, tableName);
    }
}
