package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.util.Debug.*;
import static com.napol.koltsegvetes.db.ETableNames.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum EColumnNames
{
    CA_ID(SQL_TYPE_8BYTEKEY), // folyoszamla ID (varchar: 'otp', 'kezp')
    CA_NAME("varchar(20)"), // folyoszamla neve ('Otp Bank Folyoszamla')
    CA_DATE(SQL_TYPE_DATE), // utolso valtozas datuma - VAN-E ERRE SZUKSEG?
    CA_BALANCE(SQL_TYPE_INTEGER), // utolso egyenleg - VAN-E ERRE SZUKSEG?
    CA_INSERT_DATE(SQL_TYPE_DATE),

    CL_NAME("varchar(20) primary key not null unique"), //
    CL_DIRECTION(SQL_TYPE_INTEGER), // milyen iranyba folyik a penz: ha negativ kimenet, ha pozitiv bemenet
    CL_INSERT_DATE(SQL_TYPE_DATE),

    TR_ID(SQL_TYPE_AUTOIDKEY), // tranzakcio ID (autoincrement)
    TR_DATE(SQL_TYPE_DATE), // datum
    TR_CAID(CA_ID), // folyoszamla ID
    TR_AMOUNT(SQL_TYPE_INTEGER), // osszeg
    TR_NEWBALANCE(SQL_TYPE_INTEGER), // uj egyenleg
    TR_CLNAME(CL_NAME), // a tranzakcio 'klasztere': mobilfeltoltes, napi szuksegletek, luxus stb...
    TR_REMARK("varchar(128)"), // megjegyzes, komment
    TR_INSERT_DATE(SQL_TYPE_DATE),

    /* spar, lidl, aldi, dechatlon, ikea, groby, auchan, dezsoba, izlelo, itkmenza */
    MK_ID(SQL_TYPE_8BYTEKEY), // 8 karakteres id
    MK_NAME("varchar(20)"), // neve
    MK_INSERT_DATE(SQL_TYPE_DATE), // mikor szurtam be

    PI_ID(SQL_TYPE_AUTOIDKEY), // autoincrement id
    PI_DATE(SQL_TYPE_DATE), // mikor vasaroltam
    PI_AMOUNT(SQL_TYPE_INTEGER), // mennyiert
    PI_WHAT("varchar(50)"), // mit
    PI_MKID(MK_ID), // hol
    PI_CLNAME(CL_NAME), // milyen tipusba sorolhato a vasarlas: lasd CLUSTERS
    PI_INSERT_DATE(SQL_TYPE_DATE), // mikor szurtam be

    QR_INTEGER(Integer.class),
    QR_PRETTY_DATE(String.class),
    QR_DATE(Date.class),

    NONE(Object.class);

    public static final String opLess = "<";
    public static final String opMore = ">";
    public static final String opEqual = "=";
    public static final String opLike = "like";

    public static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.getDefault());
    public static final SimpleDateFormat fancyDateFormat = new SimpleDateFormat("yyyy-MM-dd MMMM EEEE", Locale.getDefault());
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
        else if (sqltype.startsWith(SQL_TYPE_INTEGER)) javatype = Integer.class;
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
        return name().contains("DATE") && sqltype.startsWith(SQL_TYPE_DATE);
    }

    public boolean isInsertDate()
    {
        return name().equals(table.prefix() + COL_INSERT_DATE);
    }

    private String toQuote(String str)
    {
        return "'" + str + "'";
    }

    // @formatter:off
    public Object toDate(String data)
    {
        try
        {
            if (javatype != Date.class) return data;
            try { return isoDateFormat.parse(data); } catch (ParseException e) { debug(e); }
            try { return simpleDateFormat.parse(data); } catch (ParseException e) { debug(e); }
        }
        catch (NullPointerException e) { debug(e); return ""; }
        return data;
    }
    // @formatter:on

    private String toString(Object data, SimpleDateFormat format)
    {
        if (data == null) return "null";

        if (javatype == Integer.class) return data.toString();

        else if (javatype == Date.class)
        {
            if (data instanceof String) data = toDate((String) data);
            if (data instanceof Date) return format.format((Date) data);
        }

        else if (javatype == String.class) { return (String) data; }

        return null;
    }

    public String toSqlString(Object data)
    {
        String str = toString(data, isoDateFormat);
        if (str == null)
        {
            System.out.println("THIS IS NULL");
            return null;
        }

        if (javatype == Integer.class) return str;

        return toQuote(str);
    }

    public String toDisplayString(Object data)
    {
        return toString(data, simpleDateFormat);
    }

    public String sqlwhere(Object comperand, String operator)
    {
        return sqlname() + operator + toSqlString(comperand);
    }
    
    public static ArrayList<EColumnNames> getColumns(ETableNames t)
    {
        ArrayList<EColumnNames> ret = new ArrayList<EColumnNames>();
        for (EColumnNames c : values())
        {
            if (c.table == t) ret.add(c);
        }
        return ret;
    }
}
