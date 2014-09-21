package com.napol.koltsegvetes.db;

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

    INSTANCE("");

    private final String sqltype;
    
    private EColumnNames(String sqltype)
    {
        this.sqltype = sqltype;
    }
    
    public String sqlname()
    {
        return name().toLowerCase();
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
        return prefix + "." + name().toLowerCase();
    }
}
