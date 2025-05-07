package com.querydsl.sql;

import java.io.Serializable;
import java.util.Set;

public  class DsSQLTemplates extends SQLTemplates implements Serializable {
    private static final long serialVersionUID = 6645650174671640812L;

    public DsSQLTemplates() {
        super("\"", '\\', false);
    }

    public DsSQLTemplates(String quoteStr) {
        super(quoteStr, '\\', true);
    }

    public DsSQLTemplates(String quoteStr, char escape, boolean useQuotes) {
        super(quoteStr, escape, useQuotes);
    }

    public DsSQLTemplates(Set<String> reservedKeywords, String quoteStr, char escape, boolean useQuotes) {
        super(reservedKeywords, quoteStr, escape, useQuotes);
    }

    protected DsSQLTemplates(Set<String> reservedKeywords, String quoteStr, char escape, boolean useQuotes, boolean requiresSchemaInWhere) {
        super(reservedKeywords, quoteStr, escape, useQuotes, requiresSchemaInWhere);
    }

    public String quoteIdentifier(String identifier, boolean precededByDot) {
        if (identifier.matches("(?s)^\\(.*\\)$")) {
            return identifier;
        }else{
            return super.quoteIdentifier(identifier, precededByDot);
        }
    }

//    protected boolean requiresQuotes(final String identifier, final boolean precededByDot) {
//        if (identifier.matches("^\\(.*\\)$")) {
//            return false;
//        } else {
//            return super.requiresQuotes(identifier, precededByDot);
//        }
//    }

    @Override
    public void setPrintSchema(boolean printSchema) {
        super.setPrintSchema(printSchema);
    }
}
