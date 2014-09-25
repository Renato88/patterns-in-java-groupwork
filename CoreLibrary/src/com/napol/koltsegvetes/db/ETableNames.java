package com.napol.koltsegvetes.db;

import java.util.Locale;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum ETableNames
{
    CHARGE_ACCOUNTS, // folyoszamlak 
    TRANZACTIONS;    // tranzakciok
    
    public String sqlname ()
    {
        return name().toLowerCase(Locale.getDefault());
    }
    
    public String sqlname(String prefix)
    {
        return sqlname() + " as " + prefix;
    }
}
