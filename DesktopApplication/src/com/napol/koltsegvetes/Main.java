package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.CA_BALANCE;
import static com.napol.koltsegvetes.db.EColumnNames.CA_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.CL_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.QR_PRETTY_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK;
import static com.napol.koltsegvetes.db.EColumnNames.opEqual;

import java.util.HashMap;
import java.util.Map;

import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;
import com.napol.koltsegvetes.net.NetworkInterface;
import com.napol.koltsegvetes.util.MetaUtil;

/**
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class Main
{

    AbstractDataStore db = new AbstractDataStore()
    {
        @Override
        protected ISQLiteHelper getHelperInstance()
        {
            return SQLiteDriverJDBC.instance();
        }
    };

    public Main()
    {
        MetaUtil.GeneratePlantUML(SQLiteDriverJDBC.class);
        MetaUtil.GeneratePlantUML(NetworkInterface.class);
        
        db.onCreate();

        // testing if EColumnNames works good
        System.out.println(TR_CAID.ref().name());
        System.out.println(TR_AMOUNT.table().name());
        System.out.println(QR_PRETTY_DATE.table().name());
        System.out.println(CA_BALANCE.table().name());

        AbstractQuery q = db.select(TR_AMOUNT, TR_CAID, TR_CLNAME, CA_BALANCE, CA_NAME);
        
        AbstractQuery q1 = new AbstractQuery(TR_AMOUNT, TR_CLNAME, CL_NAME);
        q1.addRecord(12, "Kutyagumi", "ugyanaz");
        
        q.appendQuery(q1);
        
        for (Object[] r : q)
        {
            System.out.println("----------------------------");
            for (int i = 0; i < r.length; ++i)
            {
                EColumnNames t = q.getTypes()[i];
                System.out.println(t.name() + ": " + t.toSqlString(r[i] != null ? r[i] : "null")
                    + " - " + t.toDisplayString(r[i] != null ? r[i] : "null"));
            }
        }
        System.out.println("----------------------------");

        Map<EColumnNames, Object> row = new HashMap<>();

        row.clear();
        row.put(TR_AMOUNT, 40000);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        db.insert(row);

        row.clear();
        row.put(TR_AMOUNT, 4000);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        db.insert(row);

        row.clear();
        row.put(TR_AMOUNT, 400);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        db.insert(row);

        row.clear();
        row.put(TR_AMOUNT, 40);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        db.insert(row);

        // testing Abstract query
        AbstractQuery table = new AbstractQuery(TR_AMOUNT, TR_REMARK);
        table.addRecord(1231, "Kutyafule");

        System.out.println(table.isEmpty());
        System.out.println(table.getFirst()[0]);
        
        db.delete(ETableNames.TRANZACTIONS, TR_AMOUNT.sqlwhere(40000, opEqual));
        
        new Thread(new NetworkInterface()).start();
    }

    public static void main(String[] args)
    {
        new Main();
        System.out.println("SUCCES: The application returned successfully!");
    }

}
