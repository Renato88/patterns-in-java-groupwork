package com.napol.koltsegvetes;

import java.util.HashMap;
import java.util.Map;

import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;
import com.napol.koltsegvetes.dbinterface.AbstractQuery;
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

        DataStore db = new DataStore(driver);

        Map<EColumnNames, Object> row = new HashMap<>();
        row.put(EColumnNames.TR_AMOUNT, 40000);
        row.put(EColumnNames.TR_REMARK, "10 bax narancs nektar");

        db.insert(ETableNames.TRANZACTIONS, row);

        AbstractQuery table = new AbstractQuery().setTypes(EColumnNames.TR_AMOUNT, EColumnNames.TR_REMARK);
        table.addRecord(1231, "Kutyafule");

        System.out.println(table.isEmpty());
        System.out.println(table.getFirst()[0]);
    }

}
