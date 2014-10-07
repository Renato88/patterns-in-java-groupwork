package com.napol.koltsegvetes.db;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import com.napol.koltsegvetes.dbinterface.AbstractQuery;
import com.napol.koltsegvetes.dbinterface.ISQLCommands;
import com.napol.koltsegvetes.dbinterface.ISQLiteHelper;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class DataStore
{
    private static final String DBNAME = "koltsegvetes.db";

    ISQLiteHelper helper;

    public DataStore(ISQLiteHelper helper)
    {
        this.helper = helper;
        this.helper.onCreate();
    }

    /** 
     * Generates a string which represents the declaration of the current column f 
     * of the current table in the 'create table ...' command.
     * Eq. 'create table table_name ( ..., <b>field_name integer primary key</b>, ... )'
     * 
     * @author Péter Polcz <ppolcz@gmail.com>
     */
    private static String sqlcoldecl(Field f)
    {
        try
        {
            return ((EColumnNames) (f.get(EColumnNames.INSTANCE))).sqlname() + " " + ((EColumnNames) (f.get(EColumnNames.INSTANCE))).sqltypeall();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            System.out.println("Problem with " + f.getName());
            System.exit(0);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            System.out.println("Problem with " + f.getName());
            System.exit(0);
        }
        return "";
    }

    /** 
     * Generates a string which represents the declaration section of the 'create table' SQLite command.
     * i.e. rows like: 'column name + column type + other declarations'
     * Eg. 'create table table_name (<b>f1 varchar(2) primary key, f2 integer, ... <b>, foreign key ...)'
     * 
     * @author Péter Polcz <ppolcz@gmail.com>
     */
    private static String sqlcolsdecl(String prefix)
    {
        String ret = "";
        Field[] cols = EColumnNames.class.getDeclaredFields();
        for (Field c : cols)
            if (c.getName().startsWith(prefix)) ret = ret + sqlcoldecl(c) + ",\n";
        return ret.substring(0, ret.length() - 2);
    }

    /** 
     * This is the interface that {@link ISQLiteHelper} (i.e. {@link SQLiteDriverJDBC}) can access.
     * 
     * @author Péter Polcz <ppolcz@gmail.com>
     */
    public static ISQLCommands ISQL_COMMANDS = new ISQLCommands()
    {
        @Override
        public String[] sqlCreateTableCommands()
        {
            return new String[]
            {
                "CREATE TABLE " + ETableNames.CHARGE_ACCOUNTS.sqlname() + " ( \n" + sqlcolsdecl("CA_") + " );",
                "CREATE TABLE " + ETableNames.TRANZACTIONS.sqlname() + " ( \n" + sqlcolsdecl("TR_") + ",\n"
                    + "foreign key (" + EColumnNames.TR_CAID.sqlname() + ") "
                    + "references " + ETableNames.CHARGE_ACCOUNTS.sqlname() + "(" + EColumnNames.CA_ID.sqlname() + ") );"
            };
        }

        @Override
        public String getFilename()
        {
            return DBNAME;
        }

        @Override
        public int getVersion()
        {
            return 1;
        }
    };

    /**
     * Balazs elvileg ezt megirta
     * @param table
     * @param obj
     * @return
     */
    public boolean insert(ETableNames table, Object[] obj)
    {
        switch (table)
        {
            case CHARGE_ACCOUNTS:
            {
                // String sqlString = "...";
                // helper.execute(sqlString);
                break;
            }
            case TRANZACTIONS:
            {

                break;
            }
        }
        return true;
    }

    public int insert(ETableNames table, EColumnNames[] c, Object[] v)
    {
        if (c.length != v.length) throw new IndexOutOfBoundsException("cols.length != vals.length");

        String sql = "insert into " + table.sqlname();
        String cols = "";
        String vals = "";
        for (int i = 0; i < c.length; ++i)
        {
            cols += ", " + c[i].sqlname();
            vals += ", " + c[i].toString(v[i]);
        }
        sql = sql + " (" + cols.substring(2) + ") values (" + vals.substring(2) + ")";

        // insert into table
        helper.execSQL(sql);

        if (ETableNames.TRANZACTIONS == table)
        {
            EColumnNames id = EColumnNames.TR_ID;
            sql = String.format("select %s from %s order by %s desc limit 1", id.sqlname(), table.sqlname(), id.sqlname());
            AbstractQuery query = helper.execSQL(sql, id);
            return (Integer) query.getFirst()[0];
        }

        return 0;
    }

    public int insert(ETableNames table, Map<EColumnNames, Object> values)
    {
        EColumnNames[] cols = new EColumnNames[values.size()];
        Object[] vals = new Object[values.size()];

        int i = 0;
        for (Entry<EColumnNames, Object> entry : values.entrySet())
        {
            cols[i] = entry.getKey();
            vals[i] = entry.getValue();
            ++i;
        }

        return insert(table, cols, vals);
        // return insert(table, (EColumnNames[]) values.keySet().toArray(), values.values().toArray());

        // String sql = "insert into " + table.sqlname();
        // String cols = "";
        // String vals = "";
        // for (Map.Entry<EColumnNames, Object> entry : values.entrySet())
        // {
        // EColumnNames key = entry.getKey();
        // cols += ", " + key.sqlname();
        // vals += ", " + key.toString(entry.getValue());
        // }
        // sql = sql + " (" + cols.substring(2) + ") values (" + vals.substring(2) + ")";
        //
        // // insert into table
        // helper.execSQL(sql);
        //
        // if (ETableNames.TRANZACTIONS == table)
        // {
        // EColumnNames id = EColumnNames.TR_ID;
        // sql = String.format("select %s from %s order by %s desc limit 1", id.sqlname(), table.sqlname(), id.sqlname());
        // AbstractQuery query = helper.execSQL(sql, id);
        // return (Integer) query.getFirst()[0];
        // }
        //
        // return 0;
    }
}

// IGY KEZDTEM, DE SZERINTEM EZ IGY ELEG GAGYI
// public static final String TR_DATE = "date";
// public static final String TR_AMOUNT = "amount";
// public static final String TR_COMMENT = "comment";
// public static final String TR_ACCOUNT = "account";
// public static final String TR_GROUP = "group";

// private static String sqlname(Field f) throws IllegalArgumentException,
// IllegalAccessException
// {
// return ((EColumnNames) (f.get(EColumnNames.INSTANCE))).sqlname();
// }
//
// private static String sqltype(Field f) throws IllegalArgumentException,
// IllegalAccessException
// {
// return ((EColumnNames) (f.get(EColumnNames.INSTANCE))).sqltype();
// }

// interface Record
// {
//
// }
//
// class TranzactionRecord implements Record
// {
// long id;
// String name;
//
// public String toSqlInsert()
// {
// return "insert table ( )";
// }
// }
//
// class Folyoszamlak implements Record
// {
//
// public String toSqlInsert()
// {
//
// }
// }
