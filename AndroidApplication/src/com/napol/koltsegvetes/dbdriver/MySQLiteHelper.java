/**
 * Created on Sep 24, 2014 9:00:15 PM
 */
package com.napol.koltsegvetes.dbdriver;

import android.database.sqlite.SQLiteDatabase;

import com.napol.koltsegvetes.MainActivity;
import com.napol.koltsegvetes.dbinterface.ISQLCommands;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
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

    public void onCreate()
    {
        helper.onCreate(db);
    }

    public void onUpgrade(int newVersion)
    {
        helper.onUpgrade(db, 0, newVersion);
    }

    public void onDestroy()
    {
        // TODO
    }

    public void onOpen()
    {
        // TODO Auto-generated method stub
    }

    public void onClose()
    {
        // TODO Auto-generated method stub
    }
}

//public class MySQLiteHelper implements ISQLiteHelper
//{
//    // the Android specific SQLite database manager object
//    private SQLiteDatabase db;
//    
//    // singleton instance
//    private static ISQLiteHelper instance;
//
//    // Android specific SQLite helper
//    private AndroidSQLiteHelper helper;
//
//    public static synchronized ISQLiteHelper instance()
//    {
//        if (instance == null) instance = new MySQLiteHelper();
//        return instance;
//    }
//
//    @Override
//    public synchronized ISQLiteHelper setSqlInterface(ISQLCommands sql)
//    {
//        helper = new AndroidSQLiteHelper(MainActivity.getContext(), sql);
//        return this;
//    }
//
//    @Override
//    public void onCreate()
//    {
//        helper.onCreate(db);
//    }
//
//    @Override
//    public void onUpgrade(int newVersion)
//    {
//        helper.onUpgrade(db, 0, newVersion);
//    }
//
//    @Override
//    public void onDestroy()
//    {
//        // TODO
//    }
//
//    @Override
//    public void onOpen()
//    {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onClose()
//    {
//        // TODO Auto-generated method stub
//    }
//
//}