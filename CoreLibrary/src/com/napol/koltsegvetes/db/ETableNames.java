package com.napol.koltsegvetes.db;

import java.lang.reflect.Field;
import java.util.Locale;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum ETableNames
{
    CHARGE_ACCOUNTS("CA_"), // folyoszamlak
    TRANZACTIONS("TR_"), // tranzakciok
    NONE(".");

    private final String pref;

    private ETableNames(String pref)
    {
        this.pref = pref;
    }

    public String prefix()
    {
        return pref;
    }

    public String sqlname()
    {
        return name().toLowerCase(Locale.getDefault());
    }

    public String sqlname(String prefix)
    {
        return sqlname() + " as " + prefix;
    }

    public static ETableNames getTable(EColumnNames col)
    {
        try
        {
            for (Field f : ETableNames.class.getDeclaredFields())
            {
                if (f.isEnumConstant() && col.name().startsWith(((ETableNames) (f.get(NONE))).pref)) 
                {
                    // System.out.println(String.format("column name: %s  field name: %s, pref: %s, isEnumCons: %b"
                    // + "  startswith: %b",
                    // col.name(), ((ETableNames) (f.get(NONE))).name(),
                    // ((ETableNames) (f.get(NONE))).pref,
                    // f.isEnumConstant(),
                    // col.name().startsWith(((ETableNames) (f.get(NONE))).pref)));
                    // System.out.println(String.format("The chosen is: %s", (ETableNames) (f.get(ETableNames.NONE))));
                    return (ETableNames) (f.get(ETableNames.NONE));
                }
            }
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return NONE;
    }
}
