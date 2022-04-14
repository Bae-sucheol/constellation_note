package com.example.constellation_note;

public class Star_data
{

    private int index;
    private int constellation_id;
    private String title;
    private String content;
    private float relative_x;
    private float relative_y;
    private int parent_index;

    //setter

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void setConstellation_id(int constellation_id)
    {
        this.constellation_id = constellation_id;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public int getIndex()
    {
        return index;
    }

    public int getConstellation_id()
    {
        return constellation_id;
    }

    public String getTitle()
    {
        return title;
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
}
