package com.querydsl.sql;

import java.util.Set;

/**
 * {@code SQLServer2008Templates} is an SQL dialect for Microsoft SQL Server 2008
 *
 * @author tiwe
 *
 */
public class DsSQLServer2008Templates extends DsSQLServer2005Templates {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") //Intentional
    public static final DsSQLServer2008Templates DEFAULT = new DsSQLServer2008Templates();
    private static final long serialVersionUID = 2282631452633218688L;

    public static Builder builder() {
        return new Builder() {
            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLServer2008Templates(escape, quote);
            }
        };
    }

    public DsSQLServer2008Templates() {
        this(Keywords.SQLSERVER2008, '\\',false);
    }

    public DsSQLServer2008Templates(boolean quote) {
        this(Keywords.SQLSERVER2008, '\\',quote);
    }

    public DsSQLServer2008Templates(char escape, boolean quote) {
        this(Keywords.SQLSERVER2008, escape, quote);
    }

    protected DsSQLServer2008Templates(Set<String> keywords, char escape, boolean quote) {
        super(keywords, escape, quote);
    }

}
