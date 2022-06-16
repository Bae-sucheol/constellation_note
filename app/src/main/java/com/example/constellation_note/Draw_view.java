package com.example.constellation_note;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Draw_view extends View implements View.OnTouchListener
{
    private Context context;
    private Paint paint;
    private custom_path path;
    private int color_id;

    private ArrayList<custom_path> path_list = new ArrayList<>();
    private ArrayList<custom_path> undo_path_list = new ArrayList<>();

    public Draw_view(Context context)
    {
        super(context);

        this.context = context;

        this.setOnTouchListener(this);

        paint = new Paint();
        color_id = Color.BLACK;
        path = new custom_path();
        path.setColor_id(color_id);
        paint.setAntiAlias(true);
        paint.setColor(color_id);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        canvas.drawPath(path, paint);

        for(custom_path p : path_list)
        {
            paint.setColor(p.getColor_id());
            canvas.drawPath(p, paint);
        }

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN :
                paint.setColor(color_id);
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE :
                paint.setColor(color_id);
                x = motionEvent.getX();
                y = motionEvent.getY();

                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP :

                path_list.add(path);
                path = new custom_path();
                path.setColor_id(color_id);

                break;
        }

        invalidate();

        return true;
    }

    public void setColor_id(int color_id)
    {
        this.color_id = color_id;
        paint.setColor(color_id);
        path.setColor_id(color_id);
    }

    public void undo()
    {
        if(path_list.size() == 0)
        {
            return;
        }

        undo_path_list.add(path_list.get(path_list.size() - 1));
        path_list.remove(path_list.size() - 1);
        invalidate();
    }

    public void redo()
    {
        if(undo_path_list.size() == 0)
        {
            return;
        }

        path_list.add(undo_path_list.get(undo_path_list.size() - 1));
        undo_path_list.remove(undo_path_list.size() - 1);
        invalidate();
    }



}
