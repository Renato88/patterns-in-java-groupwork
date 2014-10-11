package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.*;
import static com.napol.koltsegvetes.db.EColumnNames.CA_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.CL_DIRECTION;
import static com.napol.koltsegvetes.db.EColumnNames.CL_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.QR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLUSTER;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.dbdriver.DataStore;
import com.napol.koltsegvetes.dbinterface.AbstractQuery;

public class TrFormActivity extends Activity
{
    private static String DEF_CA = "pkez";
    private static String DEF_CL = "Elelem";

    DataStore db;

    private DatePicker dp;
    private Spinner caid;
    private Spinner cl;
    private EditText etr;
    private EditText eta;

    private static final Calendar D_CALENDAR = Calendar.getInstance();
    private static final int D_YEAR = D_CALENDAR.get(Calendar.YEAR);
    private static final int D_MONTH = D_CALENDAR.get(Calendar.MONTH);
    private static final int D_DAY = D_CALENDAR.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trform);

        db = DataStore.instance();
        db.setContext(this);
        db.onCreate();

        AbstractQuery qcl = db.select(CL_NAME, CL_DIRECTION);
        AbstractQuery qcaid = db.select(CA_NAME, CA_ID);

        eta = (EditText) findViewById(R.id.trform_amount);
        etr = (EditText) findViewById(R.id.trform_remark);
        cl = (Spinner) findViewById(R.id.trform_cluster);
        caid = (Spinner) findViewById(R.id.trform_caid);
        dp = (DatePicker) findViewById(R.id.trform_date);

        int icl = 0, i = 0;
        ArrayAdapter<String> acl = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinned_dropdown_item);
        for (Object[] r : qcl)
        {
            acl.add(r[0] + " (" + ((Integer) r[1] < 0 ? "KI" : "BE") + ")");
            if (r[0].toString().startsWith(DEF_CL)) icl = i;
            ++i;
        }
        cl.setAdapter(acl);
        cl.setSelection(icl);

        int icaid = 0, j = 0;
        ArrayAdapter<String> acaid = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinned_dropdown_item);
        for (Object[] r : qcaid)
        {
            acaid.add(r[0] + " (" + r[1] + ")");
            if (r[1].toString().equalsIgnoreCase(DEF_CA)) icaid = j;
            j++;
        }
        caid.setAdapter(acaid);
        caid.setSelection(icaid);

        // dp.setOnLongClickListener(new OnLongClickListener()
        // {
        // @Override
        // public boolean onLongClick(View v)
        // {
        // dp.init(D_YEAR, D_MONTH, D_DAY, null);
        // return false;
        // }
        // });

        Button btn = (Button) findViewById(R.id.trform_submit);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finishAndReturn();
            }
        });
    }

    private String getDateFromDatePicker()
    {
        return QR_DATE.toString(new GregorianCalendar(dp.getYear(), dp.getMonth(), dp.getDayOfMonth()).getTime());
    }

    private void finishAndReturn()
    {
        ParcelableQuery q = new ParcelableQuery(TR_ID, TR_AMOUNT, TR_CAID, TR_CLUSTER, TR_DATE, TR_REMARK);
        q.addRecord(null, 
            Integer.parseInt(eta.getText().toString()),
            ((String) caid.getSelectedItem()).split("\\(")[0].trim(),
            ((String) cl.getSelectedItem()).split("\\(")[0].trim(),
            getDateFromDatePicker(),
            etr.getText().toString());

        Bundle b = new Bundle();
        b.putParcelable(MainActivity.KEY_ABSQR, q);

        Intent i = new Intent();
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }
}
