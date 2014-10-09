/**
 * Created on Sep 24, 2014 9:00:15 PM
 */
package com.napol.koltsegvetes.dbdriver;

import static com.napol.koltsegvetes.util.Util.debug;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.dbinterface.AbstractQuery;
import com.napol.koltsegvetes.dbinterface.ISQLCommands;
import com.napol.koltsegvetes.dbinterface.ISQLiteHelper;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 */
public class MySQLiteHelper implements ISQLiteHelper
{
    // the Android specific SQLite database manager object
    private SQLiteDatabase db;

    // singleton instance
    private static MySQLiteHelper INSTANCE;

    // Android specific SQLite helper
    private AndroidSQLiteHelper helper;

    private ISQLCommands sql = null;
    private Context context = null;

    public static synchronized MySQLiteHelper instance()
    {
        if (INSTANCE == null) INSTANCE = new MySQLiteHelper();
        return INSTANCE;
    }

    private MySQLiteHelper()
    {
        super();
    }

    public void init()
    {
        if (context == null) throw new NullPointerException("context not set");
        if (sql == null) throw new NullPointerException("sql interface not set");

        helper = new AndroidSQLiteHelper(context, sql);
        if (db == null || !db.isOpen()) db = helper.getWritableDatabase();
    }

    public synchronized MySQLiteHelper setSqlInterface(ISQLCommands sql)
    {
        this.sql = sql;
        return this;
    }

    public synchronized MySQLiteHelper setContext(Context context)
    {
        if (context != null) this.context = context;
        return this;
    }

    /**
     * This is called only once in the life cycle of the object.
     */
    public synchronized void onCreate()
    {
        init();
        helper.onCreate(db);
    }

    public synchronized void onUpgrade(int newVersion)
    {
        helper.onUpgrade(db, 0, newVersion);
    }

    public synchronized void onDestroy()
    {
        // TODO Auto-generated method stub
    }

    public synchronized void onOpen()
    {
        // TODO Auto-generated method stub
    }

    public synchronized void onClose()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public AbstractQuery execSQL(String sqlcommand, EColumnNames... cols)
    {
        Cursor cursor = db.rawQuery(sqlcommand, null);
        if (cursor.getColumnCount() != cols.length)
        {
            debug("query's column count does not match with the cols length");
            return null;
        }

        AbstractQuery ret = new AbstractQuery().setTypes(cols);
        int n = cols.length;

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Object[] obj = new Object[n];
            for (int i = 0; i < n; ++i)
            {
                if (cols[i].sqltype().startsWith("varchar"))
                {
                    obj[i] = cursor.getString(cursor.getColumnIndex(cols[i].sqlname()));
                    obj[i] = cols[i].toDate((String) obj[i]);
                }
                else
                {
                    obj[i] = cursor.getInt(cursor.getColumnIndex(cols[i].sqlname()));
                }
            }
            ret.addRecord(obj);
            cursor.moveToNext();
        }

        return ret;
    }

    @Override
    public void execSQL(String sqlcommand)
    {
        try
        {
            db.execSQL(sqlcommand);
        }
        catch (SQLiteConstraintException e)
        {
            debug("unable to insert - constraint ex", e);
        }
    }
}
