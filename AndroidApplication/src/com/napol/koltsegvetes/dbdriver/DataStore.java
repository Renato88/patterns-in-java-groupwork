package com.napol.koltsegvetes.dbdriver;

import android.content.Context;

import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.dbinterface.ISQLiteHelper;

public class DataStore extends AbstractDataStore
{
    private static DataStore INSTANCE = null;

    public static synchronized DataStore instance()
    {
        if (INSTANCE == null) INSTANCE = new DataStore();
        return INSTANCE;
    }

    /**
     * Should not be synchronized because it will be called inside a synchronized block
     */
    @Override
    protected ISQLiteHelper getHelperInstance()
    {
        return MySQLiteHelper.instance();
    }

    public void setContext(Context context)
    {
        MySQLiteHelper.instance().setContext(context);
    }
}
