package com.napol.koltsegvetes.dbinterface;

import java.util.List;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public interface ISQLCommands
{
    String getFilename();
    int getVersion();
    
    List<String> sqlCreateTableCommands();
    boolean initAfterCreate();
}
