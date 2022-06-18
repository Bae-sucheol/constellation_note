package com.example.constellation_note;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
    private int width;
    private boolean isEraser = false;

    private ArrayList<custom_path> path_list = new ArrayList<>();
    private ArrayList<custom_path> undo_path_list = new ArrayList<>();

    private Create_note parent;

    public Draw_view(Context context)
    {
        super(context);

        this.context = context;
        parent = (Create_note)context;

        this.setOnTouchListener(this);

        width = 4;

        paint = new Paint();
        color_id = Color.BLACK;
        path = new custom_path();
        path.setColor_id(color_id);
        path.setWidth(width);
        paint.setAntiAlias(true);
        paint.setColor(color_id);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        for(custom_path p : path_list)
        {
            paint.setColor(p.getColor_id());
            paint.setStrokeWidth(p.getWidth());
            canvas.drawPath(p, paint);
        }

        if(isEraser)
        {
            path.setColor_id(Color.WHITE);
            path.setWidth(100);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(100);
        }
        else
        {
            path.setColor_id(color_id);
            path.setWidth(width);
            paint.setColor(color_id);
            paint.setStrokeWidth(width);
        }

            canvas.drawPath(path, paint);

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {

        parent.drawing();

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN :

                paint.setColor(color_id);
                path.moveTo(x, y);

                break;
            case MotionEvent.ACTION_MOVE :

                x = motionEvent.getX();
                y = motionEvent.getY();

                path.lineTo(x, y);

                break;
            case MotionEvent.ACTION_UP :

                path_list.add(path);
                path = new custom_path();
                path.setColor_id(color_id);
                path.setWidth(width);

                break;
        }

        invalidate();

        return true;
    }

    public void setColor_id(int color_id)
    {
        this.color_id = color_id;
        //paint.setColor(color_id);
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

    public void setPenMode()
    {
        isEraser = false;
    }

    public void setEraserMode()
    {
        isEraser = true;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }


}
