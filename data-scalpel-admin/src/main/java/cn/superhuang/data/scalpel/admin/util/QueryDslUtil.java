package cn.superhuang.data.scalpel.admin.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.meta.Column;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataArgs;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataOrder;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.JdbcQueryDataOrderDirection;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLQuery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class QueryDslUtil {

    public static List<Map<String, Object>> queryData(DataSource dataSource, JdbcQueryDataArgs queryDataArgs) throws Exception {
        JdbcConfig jdbcConfig = queryDataArgs.getJdbcConfig();
        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());

        List<Map<String, Object>> results = new ArrayList<>();
        AtomicReference<Long> count = new AtomicReference<>(0L);

        List<String> columns = null;
        PathBuilder pathBuilder = new PathBuilder(Object.class, queryDataArgs.getTableName());
        PathBuilder[] fields = null;
        if (CollUtil.isNotEmpty(queryDataArgs.getColumns())) {
            columns = queryDataArgs.getColumns();
        } else {
            try (Connection connection = dataSource.getConnection()) {
                columns = MetaUtil.getTableMeta(MetaUtil.getCataLog(connection), jdbcDialect.getSchema(jdbcConfig), connection, queryDataArgs.getTableName()).getColumns().stream().map(Column::getName).collect(Collectors.toList());
            }
        }
        fields = columns.stream().map(column -> pathBuilder.get(column)).toArray(PathBuilder[]::new);


        Class.forName(jdbcDialect.getDriver());
        try (Connection conn = dataSource.getConnection()) {
            final SQLQuery<Object> rootSqlQuery = new SQLQuery<>(conn, jdbcDialect.getSQLTemplates());
            SQLQuery<Tuple> sqlQuery = rootSqlQuery.select(fields).from(pathBuilder.getRoot());

            Deque<BooleanExpression> specStack = new LinkedList<>();
            if (CollUtil.isNotEmpty(queryDataArgs.getFilters())) {
                BooleanExpression[] predicates = queryDataArgs.getFilters().stream().map(filter -> {
                    switch (filter.getOperator()) {
                        case "=":
                            return pathBuilder.get(pathBuilder.get(filter.getName())).eq(filter.getValue());
                        case "!=":
                            return pathBuilder.get(pathBuilder.get(filter.getName())).ne(filter.getValue());
                        case ">":
                            return pathBuilder.get(pathBuilder.getNumber(filter.getName(), Long.class)).gt((Number) filter.getValue());
                        case "<":
                            return pathBuilder.get(pathBuilder.getNumber(filter.getName(), Long.class)).loe((Number) filter.getValue());
                        case "like":
                            return pathBuilder.get(pathBuilder.getString(filter.getName())).like(filter.getValue().toString());
                        case "not like":
                            return pathBuilder.get(pathBuilder.getString(filter.getName())).notLike(filter.getValue().toString());
                        default:
                            throw new RuntimeException("不支持的条件类型:" + filter.getOperator());
                    }
                }).toArray(BooleanExpression[]::new);
                sqlQuery = sqlQuery.where(ExpressionUtils.allOf(predicates));
            }
            count.set(sqlQuery.fetchCount());
            sqlQuery = pageAndOrder(sqlQuery, pathBuilder, queryDataArgs.getPageSize(), queryDataArgs.getPageNo(), queryDataArgs.getOrders());
            List<Tuple> data = sqlQuery.fetch();
            for (Tuple tuple : data) {
                Object[] values = tuple.toArray();
                Map<String, Object> record = new HashMap<>();
                for (int i = 0; i < columns.size(); i++) {
                    record.put(columns.get(i), values[i]);
                }
                results.add(record);
            }
        }
        return results;
    }

    private static SQLQuery<Tuple> pageAndOrder(SQLQuery<Tuple> sqlQuery, PathBuilder pathBuilder, Integer pageSize, Integer pageNo, List<JdbcQueryDataOrder> orders) {
        if (pageNo == null) {
            pageNo = 0;
        }
        if (pageSize == null) {
            pageSize = 100;
        }
        sqlQuery = sqlQuery.limit(pageSize).offset(pageNo * pageSize);
        if (CollUtil.isNotEmpty(orders)) {
            OrderSpecifier[] orderSpecifiers = orders.stream().map(order -> {
                Order direction = order.getDirection() == JdbcQueryDataOrderDirection.DESC ? Order.DESC : Order.ASC;
                return new OrderSpecifier(direction, pathBuilder.get(order.getColumn()));
            }).toArray(OrderSpecifier[]::new);
            sqlQuery = sqlQuery.orderBy(orderSpecifiers);
        }
        return sqlQuery;
    }
}
