package com.napol.koltsegvetes.db;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 * Created on Oct 13, 2014 7:26:06 PM
 */
public class ParcelableQuery extends AbstractQuery implements Parcelable
{
    private static final long serialVersionUID = 4473379121176629630L;

    public ParcelableQuery(List<EColumnNames> cols)
    {
        super(cols);
    }
    
    public ParcelableQuery(EColumnNames... cols)
    {
        super(cols);
    }

    public ParcelableQuery(Parcel in)
    {
        ParcelableQuery l = (ParcelableQuery) in.readSerializable();
        setTypes(l.types);
        addAll(l);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        // dest.writeArray(types);
        dest.writeSerializable(this);
    }

    public static final Parcelable.Creator<ParcelableQuery> CREATOR = new Parcelable.Creator<ParcelableQuery>() {
        public ParcelableQuery createFromParcel (Parcel in) {
            return new ParcelableQuery(in);
        }

        public ParcelableQuery[] newArray (int size) {
            return new ParcelableQuery[size];
        }
    };


}
