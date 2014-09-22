package com.napol.koltsegvetes.db;

import java.util.LinkedList;

public abstract class QueryTable extends LinkedList<Object[]>
{
    private static final long serialVersionUID = 896112267010842564L;

    String[] colnames;
    Class<?>[] types;
    
    public void setRecordLength(int n)
    {
        types = new Class<?>[n];
    }
    
    public abstract void addLast(Object[] record);
//    {
//        android.sql.Query query;
//        query.pointToNext();
//        ret = new Object[5];
//        ret[1] = query.element[4].toString();
//        super.addLast(ret);
//    }
}
