package com.example.constellation_note;

import android.graphics.Path;

public class custom_path extends Path
{
    private int color_id;

    public void setColor_id(int color_id)
    {
        this.color_id = color_id;
    }

    public int getColor_id()
    {
        return this.color_id;
    }
}
