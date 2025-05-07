package com.querydsl.sql;

import com.querydsl.core.types.Ops;

import java.io.Serializable;
import java.sql.Types;
import java.util.HashSet;

public class DsDaMengTemplates extends DsSQLTemplates implements Serializable {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") //Intentional
    public static final DsDaMengTemplates DEFAULT = new DsDaMengTemplates();
    private static final long serialVersionUID = 204059773699516235L;

    public static Builder builder() {
        return new Builder() {
            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new DsDaMengTemplates(escape, quote);
            }
        };
    }


    private String bulkInsertTemplate = "insert all";

    private String bulkInsertSeparator = " into ";

    public DsDaMengTemplates() {
        this('\\', false);
//        setPrintSchema(true);
    }

    public DsDaMengTemplates(boolean quote) {
        this('\\', quote);
    }

    public DsDaMengTemplates(char escape, boolean quote) {
        super(new HashSet<>(), "\"", escape, quote, false);
        setParameterMetadataAvailable(false);
        setBatchCountViaGetUpdateCount(true);
        setWithRecursive("with ");
        setCountViaAnalytics(true);
        setListMaxSize(1000);
        setPrintSchema(true);

        setPrecedence(Precedence.COMPARISON, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        setPrecedence(Precedence.COMPARISON + 1, Ops.IS_NULL, Ops.IS_NOT_NULL, Ops.LIKE, Ops.LIKE_ESCAPE, Ops.BETWEEN,
                Ops.IN, Ops.NOT_IN, Ops.EXISTS);

        setPrecedence(Precedence.COMPARISON + 1, OTHER_LIKE_CASES);

        add(Ops.ALIAS, "{0} {1}");
        add(SQLOps.NEXTVAL, "{0s}.nextval");

        // String
        add(Ops.INDEX_OF, "instrb({0},{1})-1", Precedence.ARITH_LOW);
        add(Ops.INDEX_OF_2ARGS, "instrb({0},{1},{2+'1'})-1", Precedence.ARITH_LOW);
        add(Ops.MATCHES, "regexp_like({0},{1})", -1);
        add(Ops.StringOps.LOCATE, "instr({1},{0})");
        add(Ops.StringOps.LOCATE2, "instr({1},{0},{2s})");
        add(Ops.StringOps.LEFT, "substr({0},1,{1})");
        add(Ops.StringOps.RIGHT, "substr({0},-{1s},length({0}))");
        add(SQLOps.GROUP_CONCAT, "listagg({0},',')");
        add(SQLOps.GROUP_CONCAT2, "listagg({0},{1})");

        // Number
        add(Ops.MathOps.CEIL, "ceil({0})");
        add(Ops.MathOps.RANDOM, "dbms_random.value");
        add(Ops.MathOps.LN, "ln({0})");
        add(Ops.MathOps.LOG, "log({1},{0})");
        add(Ops.MathOps.COT, "(cos({0}) / sin({0}))");
        add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        add(Ops.MathOps.DEG, "({0*'180.0'} / " + Math.PI + ")");
        add(Ops.MathOps.RAD, "({0*'" + Math.PI + "'} / 180.0)");

        // Date / time
        add(Ops.DateTimeOps.DATE, "trunc({0})");

        add(Ops.DateTimeOps.WEEK, "to_number(to_char({0},'WW'))");
        add(Ops.DateTimeOps.DAY_OF_WEEK, "to_number(to_char({0},'D')) + 1");
        add(Ops.DateTimeOps.DAY_OF_YEAR, "to_number(to_char({0},'DDD'))");
        add(Ops.DateTimeOps.YEAR_WEEK, "to_number(to_char({0},'IYYY') || to_char({0},'IW'))");

        add(Ops.DateTimeOps.ADD_YEARS, "{0} + interval '{1s}' year");
        add(Ops.DateTimeOps.ADD_MONTHS, "{0} + interval '{1s}' month");
        add(Ops.DateTimeOps.ADD_WEEKS, "{0} + interval '{1s}' week");
        add(Ops.DateTimeOps.ADD_DAYS, "{0} + interval '{1s}' day");
        add(Ops.DateTimeOps.ADD_HOURS, "{0} + interval '{1s}' hour");
        add(Ops.DateTimeOps.ADD_MINUTES, "{0} + interval '{1s}' minute");
        add(Ops.DateTimeOps.ADD_SECONDS, "{0} + interval '{1s}' second");

        add(Ops.DateTimeOps.DIFF_YEARS, "trunc(months_between({1}, {0}) / 12)");
        add(Ops.DateTimeOps.DIFF_MONTHS, "trunc(months_between({1}, {0}))");
        add(Ops.DateTimeOps.DIFF_WEEKS, "round((cast({1} as date) - cast({0} as date)) / 7)");
        add(Ops.DateTimeOps.DIFF_DAYS, "round(cast({1} as date) - cast({0} as date))");
        add(Ops.DateTimeOps.DIFF_HOURS, "round((cast({1} as date) - cast({0} as date)) * 24)");
        add(Ops.DateTimeOps.DIFF_MINUTES, "round((cast({1} as date) - cast({0} as date)) * 1440)");
        add(Ops.DateTimeOps.DIFF_SECONDS, "round((cast({1} as date) - cast({0} as date)) * 86400)");

        add(Ops.DateTimeOps.TRUNC_YEAR, "trunc({0}, 'year')");
        add(Ops.DateTimeOps.TRUNC_MONTH, "trunc({0}, 'month')");
        add(Ops.DateTimeOps.TRUNC_WEEK, "trunc({0}, 'iw')");
        add(Ops.DateTimeOps.TRUNC_DAY, "trunc({0}, 'dd')");
        add(Ops.DateTimeOps.TRUNC_HOUR, "trunc({0}, 'hh')");
        add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc({0}, 'mi')");
        add(Ops.DateTimeOps.TRUNC_SECOND, "{0}"); // not truncated

        addTypeNameToCode("intervalds", -104);
        addTypeNameToCode("intervalym", -103);
        addTypeNameToCode("timestamp with local time zone", -102);
        addTypeNameToCode("timestamp with time zone", -101);
        addTypeNameToCode("long raw", Types.LONGVARBINARY);
        addTypeNameToCode("raw", Types.VARBINARY);
        addTypeNameToCode("long", Types.LONGVARCHAR);
        addTypeNameToCode("varchar2", Types.VARCHAR);

        addTypeNameToCode("number(1,0)", Types.BOOLEAN, true);
        addTypeNameToCode("number(3,0)", Types.TINYINT, true);
        addTypeNameToCode("number(5,0)", Types.SMALLINT, true);
        addTypeNameToCode("number(10,0)", Types.INTEGER, true);
        addTypeNameToCode("number(19,0)", Types.BIGINT, true);
        addTypeNameToCode("binary_float", Types.FLOAT, true);
        addTypeNameToCode("binary_double", Types.DOUBLE, true);
    }

    @Override
    public String getCastTypeNameForCode(int code) {
        switch (code) {
            case Types.DOUBLE:
                return "double precision";
            case Types.VARCHAR:
                return "varchar(4000 char)";
            default:
                return super.getCastTypeNameForCode(code);
        }
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case Types.TIMESTAMP:
            case TIMESTAMP_WITH_TIMEZONE:
                return "timestamp '" + literal + "'";
            case Types.DATE:
                return "date '" + literal + "'";
            case Types.TIME:
            case TIME_WITH_TIMEZONE:
                return "timestamp '1970-01-01 " + literal + "'";
            default:
                return super.serialize(literal, jdbcType);
        }
    }



}
