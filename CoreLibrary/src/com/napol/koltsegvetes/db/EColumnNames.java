package com.napol.koltsegvetes.db;

import java.util.Date;
import java.util.Locale;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum EColumnNames
{
    CA_ID("varchar(8) primary key not null unique"), // folyoszamla ID (varchar: 'otp', 'kezp')
    CA_NAME("varchar(20)"), // folyoszamla neve ('Otp Bank Folyoszamla')
    CA_DATE("varchar(50)"), // utolso valtozas datuma - VAN-E ERRE SZUKSEG?
    CA_BALANCE("integer"), // utolso egyenleg - VAN-E ERRE SZUKSEG?

    TR_ID("integer primary key autoincrement not null unique"), // tranzakcio ID (autoincrement)
    TR_DATE("varchar(50)"), // datum
    TR_CAID(CA_ID.sqltype()), // folyoszamla ID
    TR_AMOUNT("integer"), // osszeg
    TR_NEWBALANCE("integer"), // uj egyenleg
    TR_CLUSTER("varchar(30)"), // a tranzakcio 'klasztere': mobilfeltoltes, napi szuksegletek, luxus stb...
    TR_REMARK("varchar(300)"), // megjegyzes, komment

    QR_PRETTY_DATE(String.class),
    
    INSTANCE(Object.class);

    private final String sqltype;
    private final Class<?> javatype;
    
    private EColumnNames(Class<?> javatype)
    {
        this.sqltype = "NO_TYPE_SPECIFIED";
        this.javatype = javatype;
    }
    
    private EColumnNames(String sqltype)
    {
        this.sqltype = sqltype;

        if (isDateType())
            javatype = Date.class;
        else if (sqltype.startsWith("varchar"))
            javatype = String.class;
        else if (sqltype.startsWith("integer"))
            javatype = Integer.class;
        else
            javatype = Object.class;
    }
    
    public String sqlname()
    {
        return name().toLowerCase(Locale.getDefault());
    }

    public String sqltypeall()
    {
        return sqltype;
    }
    
    public String sqltype()
    {
        return sqltype.split(" ")[0];
    }
    
    public String sqlname(String prefix)
    {
        return prefix + "." + name().toLowerCase(Locale.getDefault());
    }
    
    public Class<?> javatype()
    {
        return javatype;
    }
    
    public boolean isDateType()
    {
        return name().contains("DATE") && sqltype.startsWith("varchar");
    }
    
    public String toString(Object data)
    {
        return data.toString();
    }
}
