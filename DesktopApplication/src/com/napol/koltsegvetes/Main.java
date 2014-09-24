package com.napol.koltsegvetes;

import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;
import com.napol.koltsegvetes.dbinterface.ISQLiteHelper;

/**
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class Main
{

    public static void main(String[] args)
    {

        // System.out.println(DataStore.ISQL_COMMANDS.sqlCreateTableCA());
        // System.out.println(DataStore.ISQL_COMMANDS.sqlCreateTableTR());

        ISQLiteHelper driver = SQLiteDriverJDBC.instance().setSqlInterface(DataStore.ISQL_COMMANDS);
        driver.onCreate();
    }

}
