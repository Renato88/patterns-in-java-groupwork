package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.util.Util.debug;

import java.lang.reflect.Field;
import java.util.ListIterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.dbdriver.DataStore;
import com.napol.koltsegvetes.dbinterface.AbstractQuery;

public class MainActivity extends ActionBarActivity
{
    static final String KEY_ABSQR = "absqr";
    static final int REQUEST_NEWTR = 8888;
    private ListView lw;

    class MyListAdapter<T> extends ArrayAdapter<T>
    {

        public MyListAdapter(Context context, int resource)
        {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            return super.getView(position, convertView, parent);
        }
    }

    class TransactionListAdapter extends ArrayAdapter<Object[]>
    {
        int resId;
        Context context;
        AbstractQuery query;

        public TransactionListAdapter(Context context, AbstractQuery query, int resId)
        {
            super(context, resId);
            Log.d("<pcz> TransactionListAdapter", "start + resId = " + resId);
            this.context = context;
            this.query = query;
            this.resId = resId;
        }

        Object getFieldValue(Field field)
        {
            try
            {
                return field.get(null);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        public AbstractQuery getQuery()
        {
            return query;
        }

        @Override
        public int getCount()
        {
            return query.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Log.d("<pcz> getView", "StartLine + position = " + position);
            View rowView = convertView;
            if (rowView == null)
            {
                // nr. of columns in the record
                int n = query.getRecordLength();

                // another approach: context.getSystemService(Context.LAYOUT...)
                rowView = LayoutInflater.from(context).inflate(resId, null);
                TextView[] holder = new TextView[n];

                String pattern = "pcz_listview_item_tw_";
                int nrOfInitialized = 0;

                // go through all static fields and check whether its name
                // matches the given pattern
                for (Field field : R.id.class.getDeclaredFields())
                {
                    if (field.getName().startsWith(pattern))
                    {
                        int i = Integer.parseInt(field.getName().replace(pattern, ""));

                        // check if the field's (i.e. textview's id) number is
                        // less
                        // than the number of columns in the query
                        if (i < n)
                        {
                            holder[i] = (TextView) rowView.findViewById((Integer) getFieldValue(field));
                            ++nrOfInitialized;
                        }
                    }
                }

                if (nrOfInitialized != n) throw new IllegalArgumentException("nr. textview != nr. columns in the query");
                rowView.setTag(holder);
            }

            TextView[] holder = (TextView[]) rowView.getTag();
            Object[] entry = query.get(position);

            for (int i = 0; i < holder.length; ++i)
            {
                holder[i].setText(query.getTypes()[i].toString(entry[i]));
            }

            return rowView;
        }
    }

    DataStore db;
    TransactionListAdapter ladapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DataStore.instance();
        db.setContext(this);
        db.onCreate();
        // insertDummyDate();

        AbstractQuery q = db.select(EColumnNames.TR_AMOUNT, EColumnNames.TR_REMARK);

        // MyListAdapter<String> listAdapter = new
        // MyListAdapter<String>(context, R.layout.mainlw_item);
        // MyListAdapter<String> listAdapter = new
        // MyListAdapter<String>(context, android.R.layout.simple_list_item_1);

        ladapter = new TransactionListAdapter(this, q, R.layout.mainlw_item);
        // MyListAdapter<String> listAdapter = new
        // MyListAdapter<String>(context, R.layout.simple_tw);
        // listAdapter.add("Proba");

        lw = (ListView) findViewById(R.id.mainlw);
        lw.setAdapter(ladapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) { return true; }
        if (id == R.id.action_newtr)
        {
            Intent i = new Intent(this, TrFormActivity.class);
            startActivityForResult(i, REQUEST_NEWTR);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            ParcelableQuery q;
            switch (requestCode)
            {
                case REQUEST_NEWTR:
                    q = (ParcelableQuery) data.getExtras().getParcelable(KEY_ABSQR);
                    if (q.size() > 0)
                    {
                        db.insert(q.getTypes(), q.getFirst());
                        ladapter.getQuery().appendQuery(q);
                        ladapter.notifyDataSetChanged();
                    }

                    // Toast.makeText(this,
                    // String.format("%s %s - %s",
                    // q.get(0)[0].toString(), q.get(0)[1].toString(), q.getTypes()[0].name()), Toast.LENGTH_LONG).show();
                    // debug("q = %s %s %s %s", q.getTypes()[0], q.getTypes()[1], q.getFirst()[0].toString(), q.getFirst()[1].toString());
                    break;

                default:
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    private void insertDummyDate()
    {
        debug("Inserting some dummy data into the sqlite datebase");
        AbstractQuery query = new AbstractQuery();
        query.setTypes(EColumnNames.TR_AMOUNT, EColumnNames.TR_REMARK);
        query.addRecord(1200, "Kajat vettem ennyiert");
        query.addRecord(3200, "Lidl-ben vasaroltam");
        query.addRecord(850, "Dezso ba menu");
        query.addRecord(40000, "Kivettem a bankbol");

        ListIterator<Object[]> it = query.listIterator();
        while (it.hasNext())
        {
            db.insert(query.getTypes(), it.next());
        }
    }
}
