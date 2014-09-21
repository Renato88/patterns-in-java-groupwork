package com.napol.koltsegvetes;

import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class Main {
    
    public static void main (String[] args) {
        
//        System.out.println(DataStore.ISQL_COMMANDS.sqlCreateTableCA());
//        System.out.println(DataStore.ISQL_COMMANDS.sqlCreateTableTR());

        SQLiteDriverJDBC driver = new SQLiteDriverJDBC(DataStore.DBNAME, DataStore.ISQL_COMMANDS);
        driver.onCreate();
    }
    
}

