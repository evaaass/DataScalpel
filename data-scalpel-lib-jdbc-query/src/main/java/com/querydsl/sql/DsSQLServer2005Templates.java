package com.querydsl.sql;


import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;

import java.util.Map;
import java.util.Set;


/**
 * {@code SQLServer2005Templates} is an SQL dialect for Microsoft SQL Server 2005
 *
 * @author tiwe
 */
public class DsSQLServer2005Templates extends DsSQLServerTemplates {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") //Intentional
    public static final DsSQLServer2005Templates DEFAULT = new DsSQLServer2005Templates();

    public static Builder builder() {
        return new Builder() {
            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLServer2005Templates(escape, quote);
            }
        };
    }

    private String topTemplate = "top ({0}) ";

    private String outerQueryStart = "select * from (\n  ";

    private String outerQueryEnd = ") a where ";

    private String limitOffsetTemplate = "rn > {0} and rn <= {1}";

    private String offsetTemplate = "rn > {0}";

    private String outerQuerySuffix = " order by rn";

    public DsSQLServer2005Templates() {
        this(Keywords.SQLSERVER2005, '\\',false);
    }

    public DsSQLServer2005Templates(boolean quote) {
        this(Keywords.SQLSERVER2005, '\\',quote);
    }

    public DsSQLServer2005Templates(char escape, boolean quote) {
        this(Keywords.SQLSERVER2005, escape, quote);
    }

    protected DsSQLServer2005Templates(Set<String> keywords, char escape, boolean quote) {
        super(keywords, escape, quote);
        //The older MSSQL Server suite doesn't support logarithms with a base other
        //than 10 and the natural logarithm, so we do it manually
        add(Ops.MathOps.LOG, "(LOG({0}) / LOG({1}))");
    }

    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        if (!forCountRow && metadata.getModifiers().isRestricting() && !metadata.getJoins().isEmpty()) {
            QueryModifiers mod = metadata.getModifiers();
            if (mod.getOffset() == null) {
                // select top ...
                metadata = metadata.clone();
                metadata.addFlag(new QueryFlag(Position.AFTER_SELECT,
                        Expressions.template(Integer.class, topTemplate, mod.getLimit())));
                context.serializeForQuery(metadata, forCountRow);
            } else {
                context.append(outerQueryStart);
                metadata = metadata.clone();
                WindowFunction<Long> rn = SQLExpressions.rowNumber().over();
                for (OrderSpecifier<?> os : metadata.getOrderBy()) {
                    rn.orderBy(os);
                }
                if (metadata.getOrderBy().isEmpty()) {
                    rn.orderBy(Expressions.currentTimestamp().asc());
                }
                FactoryExpression<?> pr = Projections.appending(metadata.getProjection(), rn.as("rn"));
                metadata.setProjection(FactoryExpressionUtils.wrap(pr));
                metadata.clearOrderBy();
                context.serializeForQuery(metadata, forCountRow);
                context.append(outerQueryEnd);
                if (mod.getLimit() == null) {
                    context.handle(offsetTemplate, mod.getOffset());
                } else {
                    context.handle(limitOffsetTemplate, mod.getOffset(), mod.getLimit() + mod.getOffset());
                }
                context.append(outerQuerySuffix);
            }

        } else {
            context.serializeForQuery(metadata, forCountRow);
        }

        if (!metadata.getFlags().isEmpty()) {
            context.serialize(Position.END, metadata.getFlags());
        }
    }

    @Override
    public void serializeDelete(QueryMetadata metadata, RelationalPath<?> entity, SQLSerializer context) {
        // limit
        QueryModifiers mod = metadata.getModifiers();
        if (mod.isRestricting()) {
            metadata = metadata.clone();
            metadata.addFlag(new QueryFlag(Position.AFTER_SELECT,
                    Expressions.template(Integer.class, topTemplate, mod.getLimit())));
        }

        context.serializeForDelete(metadata, entity);

        if (!metadata.getFlags().isEmpty()) {
            context.serialize(Position.END, metadata.getFlags());
        }
    }

    @Override
    public void serializeUpdate(QueryMetadata metadata, RelationalPath<?> entity,
                                Map<Path<?>, Expression<?>> updates, SQLSerializer context) {
        // limit
        QueryModifiers mod = metadata.getModifiers();
        if (mod.isRestricting()) {
            metadata = metadata.clone();
            metadata.addFlag(new QueryFlag(Position.AFTER_SELECT,
                    Expressions.template(Integer.class, topTemplate, mod.getLimit())));
        }

        context.serializeForUpdate(metadata, entity, updates);

        if (!metadata.getFlags().isEmpty()) {
            context.serialize(Position.END, metadata.getFlags());
        }
    }

}
