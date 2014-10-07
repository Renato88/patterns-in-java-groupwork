package com.napol.koltsegvetes;

import java.util.HashMap;
import java.util.Map;

import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;
import com.napol.koltsegvetes.dbinterface.AbstractQuery;

/**
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class Main
{

    public static void main(String[] args)
    {

        // testing if EColumnNames works good
        System.out.println(EColumnNames.TR_CAID.ref().name());
        System.out.println(EColumnNames.TR_AMOUNT.table().name());
        System.out.println(EColumnNames.QR_PRETTY_DATE.table().name());
        System.out.println(EColumnNames.CA_BALANCE.table().name());

        DataStore db = new DataStore(SQLiteDriverJDBC.instance().setSqlInterface(DataStore.ISQL_COMMANDS));
        AbstractQuery q = db.select(EColumnNames.TR_AMOUNT, EColumnNames.TR_CAID, EColumnNames.TR_CLUSTER,
            EColumnNames.CA_BALANCE, EColumnNames.CA_NAME);
        for (Object[] r : q)
        {
            System.out.println("----------------------------");
            for (int i = 0; i < r.length; ++i)
            {
                EColumnNames t = q.getTypes()[i];
                System.out.println(t.name() + ": " + t.toQuoteString(r[i] != null ? r[i] : "null")
                    + " - " + t.toString(r[i] != null ? r[i] : "null"));
            }
        }

        Map<EColumnNames, Object> row = new HashMap<>();
        
        row.clear();
        row.put(EColumnNames.TR_AMOUNT, 40000);
        row.put(EColumnNames.TR_REMARK, "10 bax narancs nektar");
        row.put(EColumnNames.TR_CAID, "potp");
        db.insert(row);
        
        row.clear();
        row.put(EColumnNames.CA_ID, "potp");
        row.put(EColumnNames.CA_NAME, "Peti Otp Bank Szamla");
        db.insert(row);
        
        row.clear();
        row.put(EColumnNames.CL_NAME, "alberlet");
        row.put(EColumnNames.CL_DIRECTION, -1);
        db.insert(row);

        row.clear();
        row.put(EColumnNames.CL_NAME, "osztondij");
        row.put(EColumnNames.CL_DIRECTION, 1);
        db.insert(row);
        
        // testing Abstract query
        AbstractQuery table = new AbstractQuery(EColumnNames.TR_AMOUNT, EColumnNames.TR_REMARK);
        table.addRecord(1231, "Kutyafule");

        System.out.println(table.isEmpty());
        System.out.println(table.getFirst()[0]);
    }

}
