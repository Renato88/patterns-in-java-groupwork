package com.napol.koltsegvetes.dbinterface;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public interface ISQLiteHelper 
{
    // life cycle: [1] functions that can modify the database
    public void onCreate();
    public void onUpgrade(int newVersion);
    public void onDestroy();
    
    // life cycle: [2] functions that cannot modify tha database
    public void onOpen();
    public void onClose();
    
    // setters:
    public ISQLiteHelper setSqlInterface(ISQLCommands sql);
}
