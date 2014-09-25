/**
 * Created on Sep 24, 2014 9:00:15 PM
 */
package com.napol.koltsegvetes.dbdriver;

import android.database.sqlite.SQLiteDatabase;

import com.napol.koltsegvetes.MainActivity;
import com.napol.koltsegvetes.dbinterface.ISQLCommands;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * Final output of the system: textured city model generated from the LiDAR point clouds and satellite images. In the model are also enhanced the detected vegetation points.
 */
public class MySQLiteHelper 
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

    public synchronized  void onUpgrade(int newVersion)
    {
        helper.onUpgrade(db, 0, newVersion);
    }

    public synchronized  void onDestroy()
    {
        // TODO Auto-generated method stub
    }

    public synchronized  void onOpen()
    {
        // TODO Auto-generated method stub
    }

    public synchronized  void onClose()
    {
        // TODO Auto-generated method stub
    }
}
