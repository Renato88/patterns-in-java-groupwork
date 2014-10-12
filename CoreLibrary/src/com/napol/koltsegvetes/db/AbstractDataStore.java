package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.EColumnNames.CA_ID;
import static com.napol.koltsegvetes.db.EColumnNames.CA_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.CL_DIRECTION;
import static com.napol.koltsegvetes.db.EColumnNames.CL_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.opEqual;
import static com.napol.koltsegvetes.util.Debug.debug;

import java.util.ArrayList;
import java.util.Date;
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
 * 
 * Important remarks:
 * [1] retrieve last insert row ID:
 *  > 'select last_insert_rowid()' --> doesn't work if a new session is started
 *  > 'select * from sqlite_sequence' --> works fine every time
 */
public abstract class AbstractDataStore
{
    private static final String DBNAME = "koltsegvetes.db";

    private ISQLiteHelper helper = null;
    private boolean created = false;

    /**
     * Should not be synchronized.
     */
    protected AbstractDataStore()
    {
        helper = getHelperInstance();
        helper.setSqlInterface(sqlInitHelper);
    }

    public synchronized void onCreate()
    {
        if (!created)
        {
            helper.onCreate();
            sqlInitHelper.initAfterCreate();
            created = true;
        }
    }
    
    public synchronized void onDestroy()
    {
        
    }

    public synchronized void onUpgrade()
    {
        
    }
    
    /**
     * Factory method of the helper
     * @return {@link ISQLiteHelper} instance to initialize this.helper
     */
    protected abstract ISQLiteHelper getHelperInstance();

    /**
     * @author Polcz Péter <ppolcz@gmail.com>
     * @param prefix
     * @return
     */
    private String sqlcolsdecl(String prefix)
    {
        String ret = "";
        EColumnNames[] cols = EColumnNames.values();
        for (EColumnNames c : cols)
            if (c.name().startsWith(prefix)) ret += c.sqlname() + " " + c.sqltypeall() + ",\n";
        for (EColumnNames c : cols)
            if (c.name().startsWith(prefix) && c.ref() != null)
            {
                ret += "foreign key (" + c.sqlname() + ") references " + c.ref().table().sqlname() + "(" + c.ref().sqlname() + ")" + ",\n";
            }
        return ret.substring(0, ret.length() - 2);
    }

    /**
     * @author Polcz Péter <ppolcz@gmail.com>
     * @param table
     * @return
     */
    private String sqlcreatetable(ETableNames table)
    {
        return "CREATE TABLE " + table.sqlname() + " ( \n" + sqlcolsdecl(table.prefix()) + " );";
    }

    /** 
     * This is the interface that {@link ISQLiteHelper} (i.e. {@link SQLiteDriverJDBC}) will be given as a callback.
     * 
     * @author Péter Polcz <ppolcz@gmail.com>
     */
    private final ISQLCommands sqlInitHelper = new ISQLCommands()
    {
        @Override
        public List<String> sqlCreateTableCommands()
        {
            ArrayList<String> l = new ArrayList<String>();

            for (ETableNames t : ETableNames.values())
            {
                if (!t.isNone()) l.add(sqlcreatetable(t));
            }

            return l;
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

        @Override
        public boolean initAfterCreate()
        {
            if (helper == null) return false;

            {
                EColumnNames[] types = { CL_NAME, CL_DIRECTION };
                Object[][] cls = {
                    { "Elelem", -1 },
                    { "Ruhazkodas", -1 },
                    { "Alberlet", -1 },
                    { "Osztondij", +1 },
                    { "Fizetes", +1 },
                    { "Egyeb kiadas", -1 },
                    { "Egyeb bevetel", +1 },
                    { "Athelyezes innen", -1 },
                    { "Athelyezes ide", +1 }
                };

                for (Object[] cl : cls)
                    insert(types, cl);
            }

            {
                EColumnNames[] types = { CA_ID, CA_NAME };
                Object[][] cls = {
                    { "potp", "Peti OTP Bank" },
                    { "pkez", "Peti kezpenz" }
                };

                for (Object[] cl : cls)
                    insert(types, cl);
            }

            return true;
        }
    }; /* end of implementation of the ISQLCommands */

    /**
     * Balazs elvileg ezt megirta
     * @param table
     * @param obj
     * @return
     */
    @Deprecated
    public synchronized boolean insert(ETableNames table, Object[] obj)
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
     * @return last_insert_rowid()
     * 
     * @author Polcz Péter <ppolcz@gmail.com>
     */
    public synchronized int insert(EColumnNames[] c, Object... v)
    {
        ETableNames table = c[0].table();
        if (c.length != v.length) throw new IndexOutOfBoundsException("cols.length != vals.length");

        String sql = "insert into " + table.sqlname();
        String cols = "";
        String vals = "";
        for (int i = 0; i < c.length; ++i)
        {
            if (c[i] == table.getInsertDateColumn()) continue;
            cols += ", " + c[i].sqlname();
            vals += ", " + c[i].toSqlString(v[i]);
        }
        
        // append insert_date column
        cols += ", " + table.getInsertDateColumn().sqlname();
        vals += ", " + table.getInsertDateColumn().toSqlString(new Date());
        
        sql = sql + " (" + cols.substring(2) + ") values (" + vals.substring(2) + ")";
        System.out.println(sql);

        // insert into table
        if (!helper.execSQL(sql)) return -1;
        return helper.lastInsertRowID();
    }

    public synchronized int insert(Map<EColumnNames, Object> values)
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

    public synchronized boolean delete(ETableNames t, String whereStatement)
    {
        String sql = "delete from " + t.sqlname() + " where " + whereStatement;
        helper.execSQL(sql);
        return true;
    }

    public synchronized AbstractQuery select(ETableNames t, String where)
    {
        String sql = "SELECT * FROM " + t.sqlname() + " WHERE " + where;
        debug(sql);

        ArrayList<EColumnNames> cols = EColumnNames.getColumns(t);
        return helper.execSQL(sql, cols.toArray(new EColumnNames[cols.size()]));
    }
    
    /**
     * @author Polcz Péter <ppolcz@gmail.com>
     * Generates a SELECT SQL command from the given columns using their natural joint product.
     * @return 
     */
    @SafeVarargs
    public synchronized final AbstractQuery select(EColumnNames... cols)
    {
        return select("", cols);
    }
    
    /**
     * @author Polcz Péter <ppolcz@gmail.com>
     * @param where
     * @param cols
     * @return
     */
    @SafeVarargs
    public synchronized final AbstractQuery select(String where, EColumnNames... cols)
    {
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

                where += " and " + c.sqlwhere(c.ref().sqlname(), opEqual);
            }
        }
        if (!where.isEmpty()) where = " WHERE " + where.substring(5);

        // generate tables list
        for (ETableNames t : tables)
            sqltables += ", " + t.sqlname();

        // generate columns list
        for (EColumnNames c : cols)
            sqlcols += ", " + c.sqlname();

        sql = String.format(sql, sqlcols.substring(2), sqltables.substring(2) + where);
        debug(sql);

        return helper.execSQL(sql, cols);
    }
}
