package com.napol.koltsegvetes.dbinterface;

import com.napol.koltsegvetes.db.EColumnNames;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public interface ISQLiteHelper 
{
    // life cycle: [1] functions that can modify the database
    void onCreate();
    void onUpgrade(int newVersion);
    void onDestroy();
    
    // life cycle: [2] functions that cannot modify the database
    void onOpen();
    void onClose();
    
    // setters:
    ISQLiteHelper setSqlInterface(ISQLCommands sql);
    
    // supported SQL commands: insert, remove, update, select
    AbstractQuery execQuery(String sqlcommand, EColumnNames cols[]);
}
