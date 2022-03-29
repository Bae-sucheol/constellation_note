package com.example.constellation_note;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class Line_star extends View
{

    private float angle;
    private int distance;
    private int thickness;

    private ViewGroup.LayoutParams line_param;

    public Line_star(Context context, float angle, int distance, int thickness)
    {
        super(context);

        line_param = new ViewGroup.LayoutParams(0, 0);
        this.setLayoutParams(line_param);

        this.setBackgroundColor(Color.WHITE);

        this.setAngle(angle);
        this.setDistance(distance);
        this.setThickness(thickness);
    }

    public void setAngle(float angle)
    {
        this.angle = angle;
        this.setRotation(angle);
    }

    public void setDistance(int distance)
    {
        this.distance = distance;
        ViewGroup.LayoutParams param =  this.getLayoutParams();
        param.width = distance;
        this.setLayoutParams(param);
    }

    public void setThickness(int thickness)
    {
        this.thickness = thickness;
        ViewGroup.LayoutParams param =  this.getLayoutParams();
        param.height = thickness;
        this.setLayoutParams(param);
    }

}
