package com.napol.koltsegvetes.db;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public interface ISQLiteHelper 
{
    public void onCreate();
    public void onUpgrade();
    public void onDestroy();
    
    public void onOpen();
    public void onClose();
}
