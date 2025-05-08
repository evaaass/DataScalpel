package cn.superhuang.data.scalpel.lib.jdbc.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.HandleHelper;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.SqlExecutor;
import cn.superhuang.data.scalpel.lib.jdbc.model.jdbc.*;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import cn.superhuang.data.scalpel.model.enumeration.ColumnTypeCategory;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import cn.superhuang.data.scalpel.spark.core.dialect.TDEngineDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.TDEngineRSDialect;
import cn.superhuang.data.scalpel.spark.core.util.ScalaUtil;
import cn.superhuang.data.scalpel.spark.core.util.SchemaUtil;
import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions;
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils;
import org.apache.spark.sql.jdbc.JdbcSQLQueryBuilder;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class JdbcDataQueryUtil {
    public static JdbcQueryDataResult queryDataBySQL(DataSource dataSource, JdbcSqlQueryDataArgs args) throws Exception {
        JdbcQueryDataResult result = new JdbcQueryDataResult();
        result.setStartTime(new Date());
        Map<String, Integer> pagerMap = new HashMap<>();
        pagerMap.put("pageNo", args.getPageNo());
        pagerMap.put("pageSize", args.getPageSize());


        JdbcConfig jdbcConfig = args.getJdbcConfig();
        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());

        Map<String, String> jdbcOptionsMap = new HashMap<>();
        jdbcOptionsMap.put(JDBCOptions.JDBC_URL(), jdbcDialect.buildUrl(jdbcConfig));
        jdbcOptionsMap.put(JDBCOptions.JDBC_DRIVER_CLASS(), jdbcDialect.getDriver());
        jdbcOptionsMap.put(JDBCOptions.JDBC_PREPARE_QUERY(), "");
        jdbcOptionsMap.put(JDBCOptions.JDBC_TABLE_NAME(), "(" + args.getSql() + ") t");
        JDBCOptions jdbcOptions = new JDBCOptions(ScalaUtil.convertToScalaImmutableMap(jdbcOptionsMap));
        JdbcSQLQueryBuilder queryBuilder = jdbcDialect.getJdbcSQLQueryBuilder(jdbcOptions).withOffset(pagerMap.get("pageNo") - 1).withLimit(pagerMap.get("pageSize")).withColumns(new String[]{"*"});

        String sql = queryBuilder.build();
        System.out.println(sql);

        AtomicReference<StructType> schemaRef = new AtomicReference<>();
        List<Entity> entities = DbUtil.use(dataSource).query(sql, (RsHandler<List<Entity>>) rs -> {
            if (schemaRef.get() == null) {
                schemaRef.set(JdbcUtils.getSchema(null, rs, jdbcDialect, true, false));
            }
            return HandleHelper.handleRs(rs, new ArrayList<>(), true);
        });


        result.setPageNo(args.getPageNo());
        result.setPageSize(args.getPageSize());
        result.setRecords(entities);
        result.setEndTime(new Date());
        if (args.getReturnCount()) {
            long count = DbUtil.use(dataSource).count(sql);
            result.setTotal(count);
        }
        if (schemaRef.get() != null) {
            List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(schemaRef.get());
            //参数名转小写
            if (CollectionUtils.isNotEmpty(columns)) {
                columns.forEach(column -> {
                    column.setName(column.getName().toLowerCase());
                });
            }
            result.setColumns(columns);
        }

        return result;
    }


    public static JdbcQueryDataResult queryData(DataSource dataSource, JdbcQueryDataArgs queryDataArgs) throws Exception {
        JdbcQueryDataResult jdbcQueryDataResult = new JdbcQueryDataResult();
        jdbcQueryDataResult.setStartTime(new Date());
        queryDataArgs.standardizing();
        Boolean isAggr = false;
        if (CollUtil.isNotEmpty(queryDataArgs.getAggregators())) {
            isAggr = true;

            queryDataArgs.getAggregators().forEach(aggregator -> {
                if (StrUtil.isBlank(aggregator.getAlias())) {
                    aggregator.setAlias(aggregator.getFunc() + "(" + (StrUtil.isBlank(aggregator.getColumn()) ? "" : aggregator.getColumn()) + ")");
                }
            });
        }

        if (isAggr) {
            queryDataArgs.getGroups().forEach(groupColumn -> {
                if (!queryDataArgs.getColumns().contains(groupColumn)) {
                    queryDataArgs.getColumns().add(groupColumn);
                }
            });
        }

        JdbcConfig jdbcConfig = queryDataArgs.getJdbcConfig();
        DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());

        AtomicReference<Long> count = new AtomicReference<>(null);

        List<Expression> fieldPathList = new ArrayList<>();


        Class.forName(jdbcDialect.getDriver());
        try (Connection conn = dataSource.getConnection()) {
            Map<String, String> options = new HashMap<>();
            options.put("url", jdbcDialect.buildUrl(jdbcConfig));
            options.put("driver", jdbcDialect.getDriver());
            if (BooleanUtil.isTrue(queryDataArgs.getSubQueryMode())) {
                options.put("dbtable", "(" + queryDataArgs.getTableName() + ") t");
            } else {
                options.put("dbtable", jdbcDialect.getTableWithSchema(queryDataArgs.getTableName(), jdbcConfig));
            }

            options.put("user", jdbcConfig.getUsername());
            options.put("password", jdbcConfig.getPassword());
            JDBCOptions jdbcOptions = new JDBCOptions(ScalaUtil.convertToScalaImmutableMap(options));
            StructType schema = JdbcUtils.getSchemaOption(conn, jdbcOptions).get();
            Map<String, StructField> columnSparkDataTypeMap = new HashMap<>();
            for (StructField field : schema.fields()) {
                columnSparkDataTypeMap.put(field.name(), field);
            }
            //判断下聚合的是不是都是数值
            if (isAggr) {
                for (JdbcQueryDataAggregator aggregator : queryDataArgs.getAggregators()) {
                    if (aggregator.getFunc() != JdbcQueryDataAggregatorFunction.COUNT) {
                        StructField structField = columnSparkDataTypeMap.get(aggregator.getColumn());
                        if (SchemaUtil.getColumnType(structField.dataType(), null).getCategory() != ColumnTypeCategory.NUMBER) {
                            throw new RuntimeException(StrUtil.format("字段{}不是数值型，不能进行聚合操作", aggregator.getColumn()));
                        }
                    }
                }
            }
            if (CollUtil.isNotEmpty(queryDataArgs.getColumns())) {
                fieldPathList.addAll(queryDataArgs.getColumns().stream().map(column -> Expressions.stringPath(column)).collect(Collectors.toList()));
            } else {
                for (StructField field : schema.fields()) {
                    fieldPathList.add(Expressions.stringPath(field.name()));
                }
            }

            for (JdbcQueryDataAggregator aggregator : queryDataArgs.getAggregators()) {
                if (aggregator.getFunc() == JdbcQueryDataAggregatorFunction.COUNT) {
                    if (StrUtil.isBlank(aggregator.getColumn())) {
                        fieldPathList.add(Expressions.as(Expressions.numberOperation(Integer.class, Ops.AggOps.COUNT_AGG, Expressions.asNumber(1)), aggregator.getAlias()));
                    } else {
                        fieldPathList.add(Expressions.as(getExpressions(columnSparkDataTypeMap.get(aggregator.getColumn()).dataType(), aggregator.getColumn()).count(), aggregator.getAlias()));
                    }
                } else {
                    //聚合函数转化下
                    DataType dataType = columnSparkDataTypeMap.get(aggregator.getColumn()).dataType();
                    ColumnType fieldType = SchemaUtil.getColumnType(dataType, null);
                    fieldPathList.add(Expressions.numberOperation(fieldType.getJavaClassType(), getAggOps(aggregator.getFunc()), Expressions.as(getExpressions(dataType, aggregator.getColumn()), aggregator.getAlias())));
                }
            }

            DsSQLTemplates dsSQLTemplates = DslSqlTemplates.getSQLTemplates(jdbcDialect);
            if (BooleanUtil.isTrue(queryDataArgs.getSubQueryMode())) {
                dsSQLTemplates.setPrintSchema(false);
            }
            final SQLQuery<Object> rootSqlQuery = new SQLQuery<>(conn, dsSQLTemplates);
            RelationalPathBase from = null;
            if (BooleanUtil.isTrue(queryDataArgs.getSubQueryMode())) {
                from = new RelationalPathBase<>(String.class, "t", jdbcDialect.getSchema(jdbcConfig), "(" + queryDataArgs.getTableName() + ")");
            } else {
                from = new RelationalPathBase<>(String.class, "t", jdbcDialect.getSchema(jdbcConfig), queryDataArgs.getTableName());
            }
            SQLQuery<Tuple> sqlQuery = rootSqlQuery.select(fieldPathList.toArray(new Expression[0]))
                    .from(from);

            Deque<BooleanExpression> specStack = new LinkedList<>();
            if (CollUtil.isNotEmpty(queryDataArgs.getFilters())) {
                //预处理，针对TIMESTAMP的查询处理
                preconditioningQueryDataArgs(queryDataArgs.getFilters(), columnSparkDataTypeMap);
                BooleanExpression[] predicates = queryDataArgs.getFilters().stream().map(filter -> {
                    StructField structField = columnSparkDataTypeMap.get(filter.getName());
                    ColumnType columnType = SchemaUtil.getColumnType(structField.dataType(), null);
                    List<Object> values = formatValue(columnType, filter.toListValue(), jdbcDialect);
                    switch (filter.getOperator()) {
                        case "in":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).in(values.toArray());
                        case "not in":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).notIn(values.toArray());
                        case "=":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).eq(values.get(0));
                        case "!=":
                            //不包含空值的。但是这种场景又需要查询出空值数据
                            BooleanExpression expression = Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).ne(values.get(0));
                            BooleanExpression isNull = Expressions.numberPath(columnType.getJavaClassType(), filter.getName()).isNull();
                            return expression.or(isNull);
                        case ">=":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).goe((Comparable) values.get(0));
                        case ">":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).gt((Comparable) values.get(0));
                        case "<":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).lt((Comparable) values.get(0));
                        case "<=":
                            return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).loe((Comparable) values.get(0));
                        case "between":
                            if (ColumnType.TIMESTAMP == columnType) {
                                BooleanExpression between = null;
                                for (int i = 0; i < values.size(); i += 2) {
                                    BooleanExpression orBetween = Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).between((Comparable) values.get(i), (Comparable) values.get(i + 1));
                                    between = between == null ? orBetween : between.or(orBetween);
                                }
                                return between;
                            } else {
                                return Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).between((Comparable) values.get(0), (Comparable) values.get(1));
                            }
                        case "not between":
                            //not between是不包含空值的。但是这种场景又需要查询出 空值数据
                            BooleanExpression notBetween = null;
                            if (ColumnType.TIMESTAMP == columnType) {
                                for (int i = 0; i < values.size(); i += 2) {
                                    BooleanExpression andNotBetween = Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).notBetween((Comparable) values.get(i), (Comparable) values.get(i + 1));
                                    notBetween = notBetween == null ? andNotBetween : notBetween.and(andNotBetween);
                                }
                            } else {
                                notBetween = Expressions.comparableEntityPath(columnType.getJavaClassType(), filter.getName()).notBetween((Comparable) values.get(0), (Comparable) values.get(1));
                            }
                            BooleanExpression aNull = Expressions.numberPath(columnType.getJavaClassType(), filter.getName()).isNull();
                            return notBetween.or(aNull);
                        case "like":
                            return Expressions.stringPath(filter.getName()).like(filter.getFirstValue().toString());
                        case "not like":
                            BooleanExpression booleanExpression = Expressions.stringPath(filter.getName()).notLike(filter.getFirstValue().toString());
                            BooleanExpression isNullExp = Expressions.numberPath(columnType.getJavaClassType(), filter.getName()).isNull();
                            return booleanExpression.or(isNullExp);
                        case "is null":
                            //对于字符类型的数据，NULL和empty在表现形式上都显示为空，但对于空字符，IS NULL无法查询到
                            if (columnType.getCategory() == ColumnTypeCategory.STRING) {
                                return Expressions.stringPath(filter.getName()).isNull().or(Expressions.stringPath(filter.getName()).isEmpty());
                            }
                            return Expressions.stringPath(filter.getName()).isNull();
                        case "is not null":
                            return Expressions.stringPath(filter.getName()).isNotNull();
                        case "is empty":
                            //.stringPath().isEmpty() 生成的sql脚本是length(name) > 0; length对于数值类型的字段查询会有问题
                            if (columnType.getCategory() == ColumnTypeCategory.NUMBER || ColumnType.TIMESTAMP.equals(columnType) || ColumnType.DATE.equals(columnType)) {
                                return Expressions.numberPath(columnType.getJavaClassType(), filter.getName()).isNull();
                            } else {
                                return Expressions.stringPath(filter.getName()).isEmpty();
                            }
                        case "is not empty":
                            if (columnType.getCategory() == ColumnTypeCategory.NUMBER || ColumnType.TIMESTAMP.equals(columnType) || ColumnType.DATE.equals(columnType)) {
                                return Expressions.numberPath(columnType.getJavaClassType(), filter.getName()).isNotNull();
                            } else {
                                return Expressions.stringPath(filter.getName()).isNotEmpty();
                            }
                        default:
                            throw new RuntimeException("不支持的条件类型:" + filter.getOperator());
                    }
                }).toArray(BooleanExpression[]::new);
                if ("or".equalsIgnoreCase(queryDataArgs.getConditionType())) {
                    //任意条件
                    sqlQuery = sqlQuery.where(ExpressionUtils.anyOf(predicates));
                } else {
                    //所有条件
                    sqlQuery = sqlQuery.where(ExpressionUtils.allOf(predicates));
                }
            }
            if (CollUtil.isNotEmpty(queryDataArgs.getGroups())) {
                sqlQuery = sqlQuery.groupBy(queryDataArgs.getGroups().stream().map(column -> Expressions.stringPath(column)).toArray(Expression[]::new));
            }
            if (Objects.isNull(queryDataArgs.getReturnCount()) || queryDataArgs.getReturnCount()) {
                count.set(sqlQuery.fetchCount());
            }

            sqlQuery = pageAndOrder(sqlQuery, jdbcDialect, queryDataArgs.getPageSize(), queryDataArgs.getPageNo(), queryDataArgs.getOrders());
            String sql = sqlQuery.getSQL().getSQL();
            System.out.println(sql);
            AtomicReference<StructType> schemaRef = new AtomicReference<>();
            List<Entity> entities = SqlExecutor.query(conn, sql, (RsHandler<List<Entity>>) rs -> {
                if (schemaRef.get() == null) {
                    schemaRef.set(JdbcUtils.getSchema(conn, rs, jdbcDialect, true, false));
                }
                return HandleHelper.handleRs(rs, new ArrayList<>(), false);
            }, sqlQuery.getSQL().getNullFriendlyBindings().toArray());
            if (dsSQLTemplates instanceof DsOracleTemplates) {
                entities.forEach(entity -> System.out.println(entity.remove("_SYS_RN_")));
            }
            jdbcQueryDataResult.setRecords(convertEntityFieldsToString(entities));
        }

        jdbcQueryDataResult.setPageNo(queryDataArgs.getPageNo());
        jdbcQueryDataResult.setPageSize(queryDataArgs.getPageSize());
        jdbcQueryDataResult.setTotal(count.get());
        jdbcQueryDataResult.setEndTime(new Date());
        return jdbcQueryDataResult;
    }

    /**
     * 对查询参数预处理
     * 目前主要是针对 TIMESTAMP类型 等于  筛选的数据处理，使其转换为范围查询
     *
     * @param filters
     * @param columnSparkDataTypeMap
     */
    private static void preconditioningQueryDataArgs(List<JdbcQueryDataFilter> filters, Map<String, StructField> columnSparkDataTypeMap) {
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        for (int i = 0; i < filters.size(); i++) {
            JdbcQueryDataFilter filter = filters.get(i);
            StructField structField = columnSparkDataTypeMap.get(filter.getName());
            ColumnType fieldType = SchemaUtil.getColumnType(structField.dataType(), null);
            //对于时间戳类型的查询,前端页面的选择器是日期，传入的值是该日的当前时间
            //但这种操作用于期望的是查询到该日的数据，故做一些转化，转成时间范围查询
            if (ColumnType.TIMESTAMP == fieldType) {
                if ("=".equals(filter.getOperator())) {
                    Long timestamp = Long.parseLong(filter.getFirstValue().toString());
                    // 获取该时间戳对应的日期
                    Date date = new Date(timestamp);
                    // 获取该时间戳当天的起始时间戳（00:00:00）
                    long startOfDayTimestamp = DateUtil.beginOfDay(date).getTime();
                    // 获取该时间戳当天的结束时间戳（23:59:59.999）
                    long endOfDayTimestamp = DateUtil.endOfDay(date).getTime();
                    filter.setOperator("between");
                    filter.setValue(Lists.newArrayList(String.valueOf(startOfDayTimestamp), String.valueOf(endOfDayTimestamp)));
                }
                if ("between".equals(filter.getOperator())) {
                    Long start = Long.parseLong(((List) filter.getValue()).get(0).toString());
                    Long end = Long.parseLong(((List) filter.getValue()).get(1).toString());
                    // 获取起始时间当天的起始时间戳（00:00:00）
                    long startOfDayTimestamp = DateUtil.beginOfDay(new Date(start)).getTime();
                    // 获取结束时间当天的结束时间戳（23:59:59.999）
                    long endOfDayTimestamp = DateUtil.endOfDay(new Date(end)).getTime();
                    filter.setValue(Lists.newArrayList(String.valueOf(startOfDayTimestamp), String.valueOf(endOfDayTimestamp)));
                }
                if ("!=".equals(filter.getOperator())) {
                    Long timestamp = Long.parseLong(filter.getFirstValue().toString());
                    // 获取该时间戳对应的日期
                    Date date = new Date(timestamp);
                    // 获取该时间戳当天的起始时间戳（00:00:00）
                    long startOfDayTimestamp = DateUtil.beginOfDay(date).getTime();
                    // 获取该时间戳当天的结束时间戳（23:59:59.999）
                    long endOfDayTimestamp = DateUtil.endOfDay(date).getTime();
                    filter.setOperator("not between");
                    filter.setValue(Lists.newArrayList(String.valueOf(startOfDayTimestamp), String.valueOf(endOfDayTimestamp)));
                }
                //这里in也需要转换成between
                //如 in "2024-01-01","2024-01-02",转换之后就会后4个值，两个为一组
                if ("in".equals(filter.getOperator())) {
                    List<String> transValueList = new ArrayList<>();
                    for (Object value : filter.toListValue()) {
                        Long timestamp = Long.parseLong(value.toString());
                        // 获取该时间戳对应的日期
                        Date date = new Date(timestamp);
                        // 获取该时间戳当天的起始时间戳（00:00:00）
                        long startOfDayTimestamp = DateUtil.beginOfDay(date).getTime();
                        // 获取该时间戳当天的结束时间戳（23:59:59.999）
                        long endOfDayTimestamp = DateUtil.endOfDay(date).getTime();
                        transValueList.add(String.valueOf(startOfDayTimestamp));
                        transValueList.add(String.valueOf(endOfDayTimestamp));
                    }
                    filter.setOperator("between");
                    filter.setValue(transValueList);
                }
                if ("not in".equals(filter.getOperator())) {
                    List<String> transValueList = new ArrayList<>();
                    for (Object value : filter.toListValue()) {
                        Long timestamp = Long.parseLong(value.toString());
                        // 获取该时间戳对应的日期
                        Date date = new Date(timestamp);
                        // 获取该时间戳当天的起始时间戳（00:00:00）
                        long startOfDayTimestamp = DateUtil.beginOfDay(date).getTime();
                        // 获取该时间戳当天的结束时间戳（23:59:59.999）
                        long endOfDayTimestamp = DateUtil.endOfDay(date).getTime();
                        transValueList.add(String.valueOf(startOfDayTimestamp));
                        transValueList.add(String.valueOf(endOfDayTimestamp));
                    }
                    filter.setOperator("not between");
                    filter.setValue(transValueList);
                }
            }
            if (ColumnType.DATE == fieldType) {
                if ("between".equals(filter.getOperator())) {
                    Long start = Long.parseLong(filter.getFirstValue().toString());
                    Long end = Long.parseLong(filter.getFirstValue().toString());
                    // 获取起始时间当天的起始时间戳（00:00:00）
                    long startOfDayTimestamp = DateUtil.beginOfDay(new Date(start)).getTime();
                    // 获取结束时间当天的结束时间戳（23:59:59.999）
                    long endOfDayTimestamp = DateUtil.endOfDay(new Date(end)).getTime();
                    filter.setValue(Lists.newArrayList(String.valueOf(startOfDayTimestamp), String.valueOf(endOfDayTimestamp)));
                }
            }
            //目前‘是否非空’条件过滤时，operator=is empty; value = 1 表示‘是’ value = 0 表示‘否’
            //此处转换非空转换成 is not empty
            if ("is empty".equals(filter.getOperator()) && !filter.isValueEmpty() && "1".equals(filter.getFirstValue().toString())) {
                filter.setOperator("is not empty");
                filter.setValue(Lists.newArrayList());
            }
            //like 和 not like 时前后加上%
            if ("like".equals(filter.getOperator()) || "not like".equals(filter.getOperator())) {
                String value = filter.getFirstValue().toString();
                Boolean containsFlag = false;
                if (value.startsWith("%") || value.endsWith("%")) {
                    containsFlag = true;
                }
                if (!containsFlag) {
                    value = "%" + value + "%";
                    filter.setValue(Lists.newArrayList(value));
                }
            }
            //operator为空时，默认设置为=
            if (StringUtils.isBlank(filter.getOperator())) {
                filter.setOperator("=");
            }
        }
    }

    /**
     * 将 List<Entity> 中的字段转换成字符串，避免精度丢失
     */
    public static List<Entity> convertEntityFieldsToString(List<Entity> entities) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            Entity processedEntity = new Entity();
            // 遍历每个字段
            for (String columnName : entity.keySet()) {
                Object value = entity.get(columnName);
                // 判断值的类型并转换成字符串
                if (value == null) {
                    processedEntity.set(columnName, null);
                } else if (value instanceof Integer || value instanceof Long || value instanceof Short) {
                    // 整数类型直接转为字符串
                    String strValue = String.valueOf(value);
                    processedEntity.set(columnName, strValue);
                } else if (value instanceof Float || value instanceof Double) {
                    // 对于浮点类型，保留适当的精度
                    processedEntity.set(columnName, value.toString());
                } else if (value instanceof BigDecimal) {
                    // 处理 BigDecimal 类型
                    processedEntity.set(columnName, ((BigDecimal) value).toPlainString());
                } else if (value instanceof Blob || value instanceof byte[]) {
                    // 处理 Blob 类型,固定展示内容;mysql 时对应的是byte[]类型
                    processedEntity.set(columnName, "<-二进制->");
                } else {
                    // 其他类型暂不处理
                    processedEntity.set(columnName, value);
                }
            }
            result.add(processedEntity);
        }

        return result;
    }


    private static SQLQuery<Tuple> pageAndOrder(SQLQuery<Tuple> sqlQuery, DsJdbcDialect jdbcDialect, Integer pageSize, Integer pageNo, List<JdbcQueryDataOrder> orders) {
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 100;
        }
        //页面从1开始。。。
        pageNo = pageNo - 1;
        sqlQuery = sqlQuery.limit(pageSize).offset(pageNo * pageSize);
        if (CollUtil.isNotEmpty(orders)) {
            OrderSpecifier[] orderSpecifiers = orders.stream().map(order -> {
                Order direction = Order.valueOf(order.getDirection().name());
                return new OrderSpecifier(direction, Expressions.stringPath(order.getColumn()));
            }).toArray(OrderSpecifier[]::new);
            sqlQuery = sqlQuery.orderBy(orderSpecifiers);
        }
        return sqlQuery;
    }

    public static SimpleExpression getExpressions(DataType dataType, String columnName) {
        ColumnType columnType = SchemaUtil.getColumnType(dataType, null);
        if (columnType.getCategory() == ColumnTypeCategory.SIMPLE) {
            return Expressions.simplePath(columnType.getJavaClassType(), columnName);
        } else if (columnType.getCategory() == ColumnTypeCategory.BOOLEAN) {
            return Expressions.booleanPath(columnName);
        } else if (columnType.getCategory() == ColumnTypeCategory.DATE) {
            return Expressions.datePath(Date.class, columnName);
        } else if (columnType.getCategory() == ColumnTypeCategory.DATETIME) {
            return Expressions.dateTimePath(Date.class, columnName);
        } else if (columnType.getCategory() == ColumnTypeCategory.NUMBER) {
            return Expressions.numberPath(columnType.getJavaClassType(), columnName);
        } else if (columnType.getCategory() == ColumnTypeCategory.STRING) {
            return Expressions.stringPath(columnName);
        }
        return Expressions.stringPath(columnName);
    }

    private static Ops.AggOps getAggOps(JdbcQueryDataAggregatorFunction func) {
        if (func == JdbcQueryDataAggregatorFunction.COUNT) {
            return Ops.AggOps.COUNT_AGG;
        } else if (func == JdbcQueryDataAggregatorFunction.MIN) {
            return Ops.AggOps.MIN_AGG;
        } else if (func == JdbcQueryDataAggregatorFunction.MAX) {
            return Ops.AggOps.MAX_AGG;
        } else if (func == JdbcQueryDataAggregatorFunction.AVG) {
            return Ops.AggOps.AVG_AGG;
        } else if (func == JdbcQueryDataAggregatorFunction.SUM) {
            return Ops.AggOps.SUM_AGG;
        }
        throw new RuntimeException("不支持的聚合类型:" + func);
    }

    private static List<Object> formatValue(ColumnType fieldType, List<Object> values, DsJdbcDialect jdbcDialect) {
        return values.stream().map(value -> {
            //对于涛思数据库时间戳查询的补丁，将时间戳转为Rfc3339格式
            if ((jdbcDialect instanceof TDEngineDialect || jdbcDialect instanceof TDEngineRSDialect)
                    && fieldType == ColumnType.TIMESTAMP) {
                if (NumberUtil.isLong(value.toString())) {
                    Long timestamp = Long.parseLong(value.toString());
                    return timestamp2Rfc3339(timestamp, "UTC+8");
                }
            }
            if (fieldType == ColumnType.DATE || fieldType == ColumnType.TIMESTAMP) {
                if (NumberUtil.isLong(value.toString())) {
                    Long timestamp = Long.parseLong(value.toString());
                    return new Date(timestamp);
                } else {
                    return DateUtil.parse(value.toString());
                }

            }
            return value;
        }).collect(Collectors.toList());
    }

    /**
     * 将时间戳转为Rfc3339时间格式
     *
     * @param timestamp
     * @param timeZone
     * @return
     */
    private static String timestamp2Rfc3339(Long timestamp, String timeZone) {
        // 将时间戳转为 Instant
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 设置目标时区
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));
        // 使用 RFC 3339 的格式化器
        DateTimeFormatter rfc3339Formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        // 格式化为 RFC 3339 格式
        String rfc3339Time = zonedDateTime.format(rfc3339Formatter);
        return rfc3339Time;
    }
}