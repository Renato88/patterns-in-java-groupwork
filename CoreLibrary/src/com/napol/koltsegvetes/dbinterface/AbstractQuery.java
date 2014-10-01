package com.napol.koltsegvetes.dbinterface;

import java.util.LinkedList;

import com.napol.koltsegvetes.db.EColumnNames;

public class AbstractQuery extends LinkedList<Object[]>
{
    private static final long serialVersionUID = 896112267010842564L;

    protected EColumnNames[] types;
    
    public EColumnNames[] getTypes ()
    {
        return types;
    }
    
    public void setTypes (EColumnNames[] types)
    {
        this.types = types;
    }
    
    public int getRecordLength()
    {
        return types.length;
    }
    
    public void setRecordLength(int n)
    {
        types = new EColumnNames[n];
    }
    
    public void checkRecord(Object[] record)
    {
        if (types == null || record.length < types.length) throw new IndexOutOfBoundsException(
            "record length smaller that expected, or not initialized");
    }
    
    public void addRecord(Object... objs)
    {
        addLast(objs);
    }
    
    @Override
    public void addLast(Object[] record)
    {
        checkRecord(record);
        super.addLast(record);
    }

//    {
//        android.sql.Query query;
//        query.pointToNext();
//        ret = new Object[5];
//        ret[1] = query.element[4].toString();
//        super.addLast(ret);
//    }
}
