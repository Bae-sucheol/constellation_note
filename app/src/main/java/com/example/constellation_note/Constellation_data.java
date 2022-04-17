package com.example.constellation_note;

import android.os.Parcel;
import android.os.Parcelable;

public class Constellation_data implements Parcelable
{

    private int id;
    private String title;

    public Constellation_data()
    {

    }

    protected Constellation_data(Parcel in) {
        id = in.readInt();
        title = in.readString();
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getId()
    {
        return this.id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public static final Creator<Constellation_data> CREATOR = new Creator<Constellation_data>()
    {
        @Override
        public Constellation_data createFromParcel(Parcel in)
        {
            return new Constellation_data(in);
        }

        @Override
        public Constellation_data[] newArray(int size)
        {
            return new Constellation_data[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(id);
        parcel.writeString(title);
    }

}
