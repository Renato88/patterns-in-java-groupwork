package com.napol.koltsegvetes.dbinterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.napol.koltsegvetes.db.EColumnNames;

public class AbstractQuery extends LinkedList<Object[]>
{
    private static final long serialVersionUID = 896112267010842564L;

    protected EColumnNames[] types;
    protected HashMap<EColumnNames, Integer> lut;

    @SafeVarargs
    public AbstractQuery(EColumnNames... types)
    {
        setTypes(types);
    }

    public AbstractQuery(List<EColumnNames> types)
    {
        setTypes(types.toArray(new EColumnNames[types.size()]));
    }

    public EColumnNames[] getTypes()
    {
        return types;
    }

    // @formatter:off
    public AbstractQuery setTypes(EColumnNames... types0)
    {
        types = types0;
        lut = new HashMap<EColumnNames, Integer>();
        for (int i = 0; i < types.length; ++i) lut.put(types[i], i);
        return this;
    }
    // @formatter:on
    
    public int getRecordLength()
    {
        return types.length;
    }

    public void checkRecord(Object[] record)
    {
        if (types == null || record.length < types.length) throw new IndexOutOfBoundsException(
            "record length smaller that expected, or not initialized");
    }

    public AbstractQuery addRecord(Object... objs)
    {
        addLast(objs);
        return this;
    }

    @Override
    public void addLast(Object[] record)
    {
        checkRecord(record);
        super.addLast(record);
    }

    public void appendQuery(AbstractQuery q)
    {
        EColumnNames[] qt = q.getTypes();

        // check if there exits matching columns
        HashSet<EColumnNames> allcols = new HashSet<EColumnNames>();
        allcols.addAll(Arrays.asList(qt));
        allcols.addAll(Arrays.asList(types));
        if (allcols.size() == qt.length + types.length) return;

        int offset = size();
        addAll(Arrays.asList(new Object[q.size()][getRecordLength()]));

        /**
         * i: this pointer
         * j: that pointer
         */
        for (int i = 0; i < types.length; ++i)
            for (int j = 0; j < qt.length; ++j)
                if (types[i] == qt[j]) for (int pos = 0; pos < q.size(); ++pos)
                    get(pos + offset)[i] = q.get(pos)[j];
    }

    // @formatter:off
    public Integer getPosition (EColumnNames c)
    { try { return lut.get(c); } catch (NullPointerException e) { return -1; } }
    // @formatter:on
}
