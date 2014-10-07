package com.napol.koltsegvetes.db;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
            EColumnNames self = (EColumnNames) (f.get(EColumnNames.INSTANCE));
            return self.sqlname() + " " + self.sqltypeall();
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
     * Detecting foreign keys from the {@link EColumnNames} declarations.
     * 
     * @author Péter Polcz <ppolcz@gmail.com>
     */
    private static String sqlforkey(Field f)
    {
        try
        {
            EColumnNames self = (EColumnNames) (f.get(EColumnNames.INSTANCE));
            EColumnNames ref = ((EColumnNames) (f.get(EColumnNames.INSTANCE))).ref();
            return ref == null ? null
                : ("foreign key (" + self.sqlname() + ") references " + ref.table().sqlname() + "(" + ref.sqlname() + ")");
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
        for (Field c : cols)
            if (c.getName().startsWith(prefix))
            {
                String fk = sqlforkey(c);
                if (fk != null) ret = ret + fk + ",\n";
            }
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
                "CREATE TABLE " + ETableNames.TRANZACTIONS.sqlname() + " ( \n" + sqlcolsdecl("TR_") + " );"
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
    @Deprecated
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

    /**
     * Insert into table
     * 
     * @param table
     * @param c
     * @param v
     * @return
     * 
     * @author Polcz Péter <ppolcz@gmail.com>
     */
    public int insert(EColumnNames[] c, Object[] v)
    {
        ETableNames table = c[0].table();
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

    @Deprecated
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

        return insert(cols, vals);
    }

    /**
     * @author Polcz Péter <ppolcz@gmail.com>
     * Generates a SELECT SQL command from the given columns using their natural joint product.
     * @return 
     */
    @SafeVarargs
    public final AbstractQuery select(EColumnNames... cols)
    {
        String sqlwhere = "";
        String sqlcols = "";
        String sqltables = "";
        String sql = "SELECT %s FROM %s";

        Set<ETableNames> tables = new HashSet<ETableNames>();

        for (EColumnNames c : cols)
        {
            tables.add(c.table());
        }

        // detecting references - generating when statement
        if (tables.size() > 1)
        {
            for (EColumnNames c : cols)
            {
                // if the current column do not references nothing
                if (c.ref() == null) continue;

                // if the table of current column's reference do no appear in the query
                if (!tables.contains(c.ref().table())) continue;

                sqlwhere += " and " + c.sqlname() + " = " + c.ref().sqlname();
            }
        }
        if (!sqlwhere.isEmpty()) sqlwhere = " WHERE " + sqlwhere.substring(5);

        // generate tables list
        for (ETableNames t : tables)
            sqltables += ", " + t.sqlname();

        // generate columns list
        for (EColumnNames c : cols)
            sqlcols += ", " + c.sqlname();

        sql = String.format(sql, sqlcols.substring(2), sqltables.substring(2) + sqlwhere);
        System.out.println(sql);
        
        return helper.execSQL(sql, cols);
    }
}
