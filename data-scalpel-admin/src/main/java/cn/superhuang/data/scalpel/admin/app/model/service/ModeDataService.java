package cn.superhuang.data.scalpel.admin.app.model.service;

import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.exception.BaseException;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelFieldRepository;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.dsl.dsl.DslParser;
import cn.superhuang.data.scalpel.dsl.dsl.DslSpecification;
import cn.superhuang.data.scalpel.dsl.dsl.DslSpecificationsBuilder;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialect;
import cn.superhuang.data.scalpel.spark.core.dialect.DsJdbcDialects;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ModeDataService {

    @Resource
    private ModelRepository modelRepository;
    @Resource
    private ModelFieldRepository modelFieldRepository;
    @Resource
    private DatasourceRepository datasourceRepository;

    public Page<Map<String, Object>> searchData(String modelId, String search, String pager, String sort) {
        List<Map<String, Object>> results = new ArrayList<>();
        AtomicReference<Long> count = new AtomicReference<>(0L);
        modelRepository.findById(modelId).ifPresentOrElse(model -> {
            datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                try {
                    PathBuilder pathBuilder = new PathBuilder(Object.class, model.getName());
                    List<ModelField> modelFields = modelFieldRepository.findAllByModelId(modelId);
                    PathBuilder[] fields = modelFields.stream().map(modelField -> pathBuilder.get(modelField.getName())).toArray(PathBuilder[]::new);
                    JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                    DsJdbcDialect jdbcDialect = DsJdbcDialects.get(jdbcConfig.getDbType());
                    Class.forName(jdbcDialect.getDriver());
                    try (Connection conn = DriverManager.getConnection(jdbcDialect.buildUrl(jdbcConfig), jdbcConfig.getUsername(), jdbcConfig.getPassword())) {
                        final SQLQuery<Object> rootSqlQuery = new SQLQuery<>(conn, jdbcDialect.getSQLTemplates());
                        SQLQuery<Tuple> sqlQuery = rootSqlQuery.select(fields).from(pathBuilder.getRoot());
                        if (StrUtil.isNotBlank(search)) {
                            BooleanExpression specification = new DslSpecificationsBuilder().build(pathBuilder, new DslParser().parse(search), DslSpecification::new);
                            sqlQuery = sqlQuery.where(specification);
                        }
                        count.set(sqlQuery.fetchCount());
                        sqlQuery = pageAndOrder(sqlQuery, pathBuilder, pager, sort);
                        List<Tuple> data = sqlQuery.fetch();
                        data.forEach(tuple -> {
                            Object[] values = tuple.toArray();
                            Map<String, Object> record = new HashMap<>();
                            for (int i = 0; i < modelFields.size(); i++) {
                                record.put(modelFields.get(i).getName(), values[i]);
                            }
                            results.add(record);
                        });
                    }
                } catch (Exception e) {
                    throw new BaseException("查询模型数据失败:" + e.getMessage(), e);
                }
            }, () -> {
                throw new BaseException("模型关联的数据存储%s不存在".formatted(model.getDatasourceId()));
            });
        }, () -> {
            throw new BaseException("模型%s不存在".formatted(modelId));
        });
        return new PageImpl<>(results, getPageRequest(pager), count.get());
    }

    private PageRequest getPageRequest(String pager) {
        int page = 0;
        int size = 20;
        if (pager != null && !pager.isEmpty()) {
            String[] limitParts = pager.split(",");
            if (limitParts.length == 1) {
                size = Integer.parseInt(limitParts[0]);
            } else if (limitParts.length == 2) {
                page = Integer.parseInt(limitParts[0]);
                size = Integer.parseInt(limitParts[1]);
            }
        }
        return PageRequest.of(page, size);
    }

    private SQLQuery<Tuple> pageAndOrder(SQLQuery<Tuple> sqlQuery, PathBuilder pathBuilder, String pager, String sort) {
        PageRequest pageRequest = getPageRequest(pager);
        OrderSpecifier[] orders = null;
        // 解析 sort 字符串
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            orders = new OrderSpecifier[sortParts.length];
            for (int i = 0; i < sortParts.length; i++) {
                String property = sortParts[i].startsWith("-") ? sortParts[i].substring(1) : sortParts[i];
                Order direction = sortParts[i].startsWith("-") ? Order.DESC : Order.ASC;
                orders[i] = new OrderSpecifier(direction, pathBuilder.get(property));
            }
        }
        sqlQuery = sqlQuery.limit(pageRequest.getPageSize()).offset(pageRequest.getOffset());
        if (orders != null) {
            sqlQuery = sqlQuery.orderBy(orders);
        }
        return sqlQuery;
    }
}
