/**
 * Created on Sep 24, 2014 9:00:15 PM
 */
package com.napol.koltsegvetes.dbdriver;

import static com.napol.koltsegvetes.util.Util.debug;

import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.napol.koltsegvetes.MainActivity;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
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
    private static MySQLiteHelper instance;

    // Android specific SQLite helper
    private AndroidSQLiteHelper helper;

    public static synchronized MySQLiteHelper instance()
    {
        if (instance == null) instance = new MySQLiteHelper();
        return instance;
    }

    public synchronized MySQLiteHelper setSqlInterface(ISQLCommands sql)
    {
        helper = new AndroidSQLiteHelper(MainActivity.getContext(), sql);
        if (db == null || !db.isOpen()) db = helper.getWritableDatabase();
        return this;
    }

    public synchronized void onCreate()
    {
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
        db.execSQL(sqlcommand);
    }
}
