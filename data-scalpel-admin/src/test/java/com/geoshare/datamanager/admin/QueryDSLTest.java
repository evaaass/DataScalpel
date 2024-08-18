package cn.superhuang.data.scalpel.admin;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryDSLTest {
    public static void main(String[] args) {


        Map<String, SQLTemplates> sqlTemplatesMap = new LinkedHashMap<>();
//        sqlTemplatesMap.put("test", SQLTemplates.DEFAULT);
//        sqlTemplatesMap.put("mysql", MySQLTemplates.DEFAULT);
//        sqlTemplatesMap.put("oracle", OracleTemplates.DEFAULT);
//        sqlTemplatesMap.put("sqlserver", SQLServer2008Templates.DEFAULT);
        sqlTemplatesMap.put("postgresql", PostgreSQLTemplates.DEFAULT);
//        sqlTemplatesMap.put("h2", H2Templates.DEFAULT);
//        sqlTemplatesMap.put("db2", DB2Templates.DEFAULT);

        for (Map.Entry<String, SQLTemplates> entry : sqlTemplatesMap.entrySet()) {
            SQLQueryFactory queryFactory = new SQLQueryFactory(PostgreSQLTemplates.DEFAULT, () -> {
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://10.0.0.5:5432/test", "postgres", "Gistack@123");
                    return conn;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });  // 初始化 SQLQueryFactory

//            final SQLQuery<Object> sqlQuery = new SQLQuery<>(entry.getValue());
            PathBuilder pathBuilder = new PathBuilder(Object.class, "sys_user");

            System.out.println("name:" + entry.getKey() + "#####################################################");
            final SQLQuery<Object> sqlQuery = (SQLQuery<Object>) queryFactory.query();
            List<Tuple> results = sqlQuery.select(pathBuilder.get("id"), pathBuilder.get("dept_id"), pathBuilder.get("user_no"), pathBuilder.get("login_name"))
                    .from(pathBuilder.getRoot())
                    .where(pathBuilder.getNumber("id", Object.class).loe(10000).and(pathBuilder.getNumber("dept_id", Object.class).loe(10000)))
//                    .where(pathBuilder.get("idnumber").in("a", "b", "c").and(pathBuilder.getString("name").like("%superhuang%")))
                    .offset(0).limit(999)
                    .orderBy(new OrderSpecifier(Order.DESC, pathBuilder.get("id")))
                    .fetch();

            for (Tuple result : results) {
                System.out.println(result);
            }
//            final SQLBindings bindings = sqlQuery.getSQL();
//
//            System.out.println(bindings.getSQL());
//            System.out.println(bindings.getNullFriendlyBindings());
        }
    }
}
