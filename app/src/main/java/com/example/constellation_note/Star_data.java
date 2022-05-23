package com.example.constellation_note;

import android.os.Parcel;
import android.os.Parcelable;

public class Star_data extends Constellation_data implements Parcelable
{

    private int id;
    private String title;
    private int constellation_id;
    private String content;
    private float relative_x;
    private float relative_y;
    private int parent_index;

    public Star_data()
    {

    }

    protected Star_data(Parcel in)
    {
        id = in.readInt();
        title = in.readString();
        constellation_id = in.readInt();
        content = in.readString();
        relative_x = in.readFloat();
        relative_y = in.readFloat();
        parent_index = in.readInt();
    }

    //setter

    public void set_id(int id)
    {
        this.id = id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setConstellation_id(int constellation_id)
    {
        this.constellation_id = constellation_id;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setRelative_x(float relative_x)
    {
        this.relative_x = relative_x;
    }

    public void setRelative_y(float relative_y)
    {
        this.relative_y = relative_y;
    }

    public void setParent_index(int parent_index)
    {
        this.parent_index = parent_index;
    }

    //getter

    public int get_id()
    {
        return this.id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public int getConstellation_id()
    {
        return constellation_id;
    }

    public String getContent()
    {
        return content;
    }

    public float getRelative_x()
    {
        return relative_x;
    }

    public float getRelative_y()
    {
        return relative_y;
    }

    public int getParent_index()
    {
        return parent_index;
    }

    public static final Creator<Star_data> CREATOR = new Creator<Star_data>()
    {
        @Override
        public Star_data createFromParcel(Parcel in)
        {
            return new Star_data(in);
        }

        @Override
        public Star_data[] newArray(int size)
        {
            return new Star_data[size];
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
        parcel.writeInt(constellation_id);
        parcel.writeString(content);
        parcel.writeFloat(relative_x);
        parcel.writeFloat(relative_y);
        parcel.writeInt(parent_index);
    }

}
