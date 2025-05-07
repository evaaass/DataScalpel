package com.querydsl.sql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;

public class TDEngineRSTemplates extends DsSQLTemplates implements Serializable {

    protected static final Expression<?> LOCK_IN_SHARE_MODE;
    public static final TDEngineRSTemplates DEFAULT;

    public static Builder builder() {
        return new Builder() {
            protected SQLTemplates build(char escape, boolean quote) {
                return new TDEngineRSTemplates(escape, quote);
            }
        };
    }

    public TDEngineRSTemplates() {
        this('\\', false);
    }

    public TDEngineRSTemplates(boolean quote) {
        this('\\', quote);
    }

    public TDEngineRSTemplates(char escape, boolean quote) {
        super(new HashSet<>(), "`", escape, quote, false);
        this.setPrintSchema(true);
        this.setArraysSupported(false);
        this.setParameterMetadataAvailable(false);
        this.setLimitRequired(true);
        this.setSupportsUnquotedReservedWordsAsIdentifier(true);
        this.setNullsFirst((String)null);
        this.setNullsLast((String)null);
        this.setForShareSupported(true);
        this.setForShareFlag(new QueryFlag(QueryFlag.Position.END, LOCK_IN_SHARE_MODE));
        this.setPrecedence(50, new Operator[]{Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE});
        this.setPrecedence(70, new Operator[]{Ops.BETWEEN});
        this.add(SQLOps.LOCK_IN_SHARE_MODE, "\nlock in share mode");
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.CONCAT, "concat({0}, {1})", -1);
        this.add(Ops.StringOps.LPAD, "lpad({0},{1},' ')");
        this.add(Ops.StringOps.RPAD, "rpad({0},{1},' ')");
        if (escape == '\\') {
            this.add(Ops.LIKE, "{0} like {1}");
            this.add(Ops.ENDS_WITH, "{0} like {%1}");
            this.add(Ops.ENDS_WITH_IC, "{0l} like {%%1}");
            this.add(Ops.STARTS_WITH, "{0} like {1%}");
            this.add(Ops.STARTS_WITH_IC, "{0l} like {1%%}");
            this.add(Ops.STRING_CONTAINS, "{0} like {%1%}");
            this.add(Ops.STRING_CONTAINS_IC, "{0l} like {%%1%%}");
        }

        this.add(Ops.MathOps.LOG, "log({1},{0})");
        this.add(Ops.MathOps.COSH, "(exp({0}) + exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.SINH, "(exp({0}) - exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.TANH, "(exp({0*'2'}) - 1) / (exp({0*'2'}) + 1)");
        this.add(Ops.AggOps.BOOLEAN_ANY, "bit_or({0})", 0);
        this.add(Ops.AggOps.BOOLEAN_ALL, "bit_and({0})", 0);
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "dayofweek({0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "dayofyear({0})");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "extract(year_month from {0})");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "yearweek({0})");
        this.add(Ops.DateTimeOps.ADD_YEARS, "date_add({0}, interval {1s} year)");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "date_add({0}, interval {1s} month)");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "date_add({0}, interval {1s} week)");
        this.add(Ops.DateTimeOps.ADD_DAYS, "date_add({0}, interval {1s} day)");
        this.add(Ops.DateTimeOps.ADD_HOURS, "date_add({0}, interval {1s} hour)");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "date_add({0}, interval {1s} minute)");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "date_add({0}, interval {1s} second)");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "timestampdiff(year,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "timestampdiff(month,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "timestampdiff(week,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "timestampdiff(day,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "timestampdiff(hour,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "timestampdiff(minute,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "timestampdiff(second,{0},{1})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "str_to_date(concat(date_format({0},'%Y'),'-1-1'),'%Y-%m-%d')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "str_to_date(concat(date_format({0},'%Y-%m'),'-1'),'%Y-%m-%d')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "str_to_date(concat(date_format({0},'%Y-%u'),'-1'),'%Y-%u-%w')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "str_to_date(date_format({0},'%Y-%m-%d'),'%Y-%m-%d')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "str_to_date(date_format({0},'%Y-%m-%d %k'),'%Y-%m-%d %k')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "str_to_date(date_format({0},'%Y-%m-%d %k:%i'),'%Y-%m-%d %k:%i')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "str_to_date(date_format({0},'%Y-%m-%d %k:%i:%s'),'%Y-%m-%d %k:%i:%s')");
        this.addTypeNameToCode("bool", -7, true);
        this.addTypeNameToCode("tinyint unsigned", -6);
        this.addTypeNameToCode("bigint unsigned", -5);
        this.addTypeNameToCode("long varbinary", -4, true);
        this.addTypeNameToCode("mediumblob", -4);
        this.addTypeNameToCode("longblob", -4);
        this.addTypeNameToCode("blob", -4);
        this.addTypeNameToCode("tinyblob", -4);
        this.addTypeNameToCode("long varchar", -1, true);
        this.addTypeNameToCode("mediumtext", -1);
        this.addTypeNameToCode("longtext", -1);
        this.addTypeNameToCode("text", -1);
        this.addTypeNameToCode("tinytext", -1);
        this.addTypeNameToCode("integer unsigned", 4);
        this.addTypeNameToCode("int", 4);
        this.addTypeNameToCode("int unsigned", 4);
        this.addTypeNameToCode("mediumint", 4);
        this.addTypeNameToCode("mediumint unsigned", 4);
        this.addTypeNameToCode("smallint unsigned", 5);
        this.addTypeNameToCode("float", 7, true);
        this.addTypeNameToCode("double precision", 8, true);
        this.addTypeNameToCode("real", 8);
        this.addTypeNameToCode("enum", 12);
        this.addTypeNameToCode("set", 12);
        this.addTypeNameToCode("datetime", 93, true);
    }

    public String escapeLiteral(String str) {
        StringBuilder builder = new StringBuilder();
        char[] var3 = super.escapeLiteral(str).toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char ch = var3[var5];
            if (ch == '\\') {
                builder.append("\\");
            }

            builder.append(ch);
        }

        return builder.toString();
    }

    public String getCastTypeNameForCode(int code) {
        switch (code) {
            case -6:
            case -5:
            case 4:
            case 5:
                return "signed";
            case -4:
            case -3:
            case -2:
            case -1:
            case 0:
            case 1:
            case 2:
            case 9:
            case 10:
            case 11:
            default:
                return super.getCastTypeNameForCode(code);
            case 3:
            case 6:
            case 7:
            case 8:
                return "decimal";
            case 12:
                return "char";
        }
    }

    static {
        LOCK_IN_SHARE_MODE = ExpressionUtils.operation(Object.class, SQLOps.LOCK_IN_SHARE_MODE, Collections.emptyList());
        DEFAULT = new TDEngineRSTemplates();
    }
}
