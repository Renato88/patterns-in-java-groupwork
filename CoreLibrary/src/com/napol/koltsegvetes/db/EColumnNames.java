package com.napol.koltsegvetes.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    TR_CAID(CA_ID), // folyoszamla ID
    TR_AMOUNT("integer"), // osszeg
    TR_NEWBALANCE("integer"), // uj egyenleg
    TR_CLUSTER("varchar(30)"), // a tranzakcio 'klasztere': mobilfeltoltes, napi szuksegletek, luxus stb...
    TR_REMARK("varchar(300)"), // megjegyzes, komment

    CL_NAME("varchar(20) primary key not null unique"), //
    CL_DIRECTION("integer"), // milyen iranyba folyik a penz: ha negativ kimenet, ha pozitiv bemenet

    QR_INTEGER(Integer.class),
    QR_PRETTY_DATE(String.class),
    QR_DATE(Date.class),

    INSTANCE(Object.class);

    public static final String opLess = "<";
    public static final String opMore = ">";
    public static final String opEqual = "=";
    public static final String opLike = "like";
    
    public static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat fancyDateFormat = new SimpleDateFormat("yyyy-MM-dd MMMM EEEE", Locale.getDefault());
    private final String sqltype;
    private final Class<?> javatype;
    private final ETableNames table;
    private final EColumnNames ref;

    private EColumnNames(EColumnNames col)
    {
        this.sqltype = col.sqltype();
        this.javatype = col.javatype;
        this.table = ETableNames.getTable(this);
        this.ref = col;
    }

    private EColumnNames(Class<?> javatype)
    {
        this.sqltype = "NO_TYPE_SPECIFIED";
        this.javatype = javatype;
        this.table = ETableNames.NONE;
        this.ref = null;
    }

    private EColumnNames(String sqltype)
    {
        this.sqltype = sqltype;
        this.table = ETableNames.getTable(this);
        this.ref = null;

        if (isDateType()) javatype = Date.class;
        else if (sqltype.startsWith("varchar")) javatype = String.class;
        else if (sqltype.startsWith("integer")) javatype = Integer.class;
        else javatype = Object.class;
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

    public ETableNames table()
    {
        return this.table;
    }

    public EColumnNames ref()
    {
        return this.ref;
    }

    public boolean isDateType()
    {
        return name().contains("DATE") && sqltype.startsWith("varchar");
    }

    private String toQuote(String str)
    {
        return "'" + str + "'";
    }

    public Object toDate(String data)
    {
        try
        {
            if (javatype != Date.class) throw new ParseException("This is not a date type (data != Date.class)", 0);
            return defaultDateFormat.parse(data);
        }
        catch (NullPointerException e)
        {
            return "";
        }
        catch (ParseException e)
        {
            StackTraceElement el = e.getStackTrace()[0];
            System.out.println(el.getClassName() + ":" + el.getLineNumber());
            el = e.getStackTrace()[1];
            System.out.println("   - " + el.getClassName() + ":" + el.getLineNumber());
        }
        return data;
    }

    public String toString(Object data)
    {
        if (data == null) return "null";
        
        if (javatype == Integer.class)
        {
            return data.toString();
        }

        else if (javatype == Date.class)
        {
            if (data instanceof String) data = toDate((String) data);
            if (data instanceof Date) return defaultDateFormat.format((Date) data);
        }

        else if (javatype == String.class)
        {
            return (String) data;
        }

        return null;
    }

    public String toQuoteString(Object data)
    {
        String str = toString(data);
        if (str == null)
        {
            System.out.println("THIS IS NULL");
            return null;
        }

        if (javatype == Integer.class) return str;

        return toQuote(str);
    }
    
    public String sqlwhere(Object comperand, String operator)
    {
        return sqlname() + operator + toQuoteString(comperand);
    }
}
