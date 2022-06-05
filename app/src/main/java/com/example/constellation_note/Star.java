package com.example.constellation_note;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;

public class Star extends View implements View.OnLongClickListener, View.OnClickListener
{
    // Star 클래스와 Star_data 클래스의 내용이 겹치므로
    // 내일은 Star 클래스와 Star_data 클래스의 중첩 부분을 제거하고 잘 활용해야 겠다.

    // 별의 번호
    private int index;

    // 별들의 사이즈.
    private static int size;

    // 별의 제목
    private String title;

    // 내용
    private String content;

    // 별 제목을 표시할 텍스트 뷰
    private TextView title_view;

    // 레이아웃 파라미터
    ViewGroup.LayoutParams star_param;
    LinearLayout.LayoutParams title_view_param;



    // 별의 위치
    // 별의 위치는 각자 다르기 때문에.
    // 절대 위치가 아니라 상대 위치로 알아야 하기 때문에 비율(소숫점)로 정해야한다.
    private float relative_x;
    private float relative_y;

    // 부모만 알면 된다.
    // 첫 로딩 시. 번호가 낮은(루트 노드와 가까운) 순서대로 불러올 예정이기 때문에...
    private int parent_index;
    private Star parent;

    private Constellation_view constellation;

    private Line_star line;

    // sql 컨트롤 객체
    private SQLiteControl sqLiteControl;

    public Star(Context context, Constellation_view constellation)
    {
        super(context);

        this.constellation = constellation;

        this.calculate_size();

        this.setBackgroundResource(R.drawable.star_glow);

        star_param = new ViewGroup.LayoutParams(size, size);

        this.setLayoutParams(star_param);

        this.setOnLongClickListener(this);
        this.setOnClickListener(this);

        title_view = new TextView(context);
        title_view.setTextColor(Color.WHITE);

        title_view_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title_view.setLayoutParams(title_view_param);
        title_view.setAlpha(0);

        constellation.addView(title_view);

        Handler handler = MainHandler.getMainHandler(context);

        sqLiteControl = new SQLiteControl(SQLiteHelper.getSqLiteHelper(getContext()), handler);
    }

    public interface Callback_star
    {
        public void click_star();
    }

    public void calculate_size()
    {
        this.size = constellation.get_width() / 20;
    }

    public void setSize()
    {
        ViewGroup.LayoutParams param =  this.getLayoutParams();
        param.width = this.size;
        param.height = this.size;
        this.setLayoutParams(param);
    }

    public int getSize()
    {
        return this.size;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getIndex()
    {
        return this.index;
    }

    public void set_Postion(float x, float y)
    {
        this.setX(x - size / 2);
        this.setY(y - size / 2);
    }

    public void setParent_index(int parent_index)
    {
        this.parent_index = parent_index;
    }

    public int getParent_index()
    {
        return this.parent_index;
    }

    public float get_x()
    {
        return this.getX() + size / 2;
    }

    public float get_y()
    {
        return this.getY() + size / 2;
    }

    public void setParent(Star parent)
    {
        this.parent = parent;
    }

    public void setTitle_view()
    {
        if(title_view.getAlpha() == 0)
        {
            title_view.setAlpha(1);
            setTitle_position();
        }
        else
        {
            title_view.setAlpha(0);
        }
    }

    public void setTitle_position()
    {
        title_view.setX(constellation.get_width() * this.relative_x - title_view.getWidth() / 2 + size / 2);
        title_view.setY(constellation.get_height() * this.relative_y + size);
    }

    public void remove_line()
    {
        if(line == null)
        {
            return;
        }

        constellation.removeView(line);
        line = null;

    }

    public void remove_title()
    {
        if(title_view == null)
        {
            return;
        }

        constellation.removeView(title_view);
        title_view = null;
    }

    public void calculate_relative_position()
    {
        this.relative_x = this.getX() / (float)constellation.get_width();
        this.relative_y = this.getY() / (float)constellation.get_height();
    }

    public void set_Position_relative()
    {
        this.setX(constellation.get_width() * this.relative_x);
        this.setY(constellation.get_height() * this.relative_y);
    }

    public void setRelative_x(float x)
    {
        this.relative_x = x;
    }

    public void setRelative_y(float y)
    {
        this.relative_y = y;
    }

    public void setTitle(String title)
    {
        this.title = title;
        title_view.setText(title);
    }

    public void setTitleAlpha(float alpha)
    {
        title_view.setAlpha(alpha);
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return this.content;
    }

    public void draw_line()
    {
        if(parent == null)
        {
            return;
        }

        double delta_x = parent.get_x() - this.get_x();
        double delta_y = parent.get_y() - this.get_y();

        float add_x = parent.get_x() + this.get_x();
        float add_y = parent.get_y() + this.get_y();

        float angle = (float)Math.toDegrees(Math.atan2(delta_y, delta_x));

        int distance = (int)Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));

        int thickness = 2;

        line = new Line_star(getContext(), angle, distance, thickness);

        line.setX(add_x / 2 - distance / 2);
        line.setY(add_y / 2);

        constellation.addView(line);

    }

    public void insert_into_star()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", this.index);
        contentValues.put("title", "임시 제목");
        contentValues.put("content", "임시 내용");
        contentValues.put("x", this.relative_x);
        contentValues.put("y", this.relative_y);
        contentValues.put("constellation_id", constellation.get_id());// 임시.

        if(this.parent != null)
        {
            contentValues.put("parent_id", this.parent.getIndex());
        }

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_INSERT, sqLiteControl.getTable_note(), contentValues));
        MainActivity.submitRunnable(sqLiteControl);

    }

    public void update_star()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("x", this.relative_x);
        contentValues.put("y", this.relative_y);

        String selection = "id = ? and constellation_id = ?";
        String selectionArgs[] = {Integer.toString(index), Integer.toString(constellation.get_id())};

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_note(), contentValues, selection, selectionArgs));
        MainActivity.submitRunnable(sqLiteControl);
    }

    @Override
    public boolean onLongClick(View view)
    {

        constellation.long_click_star(view);

        return true;

    }

    @Override
    public void onClick(View view)
    {

        constellation.click_star(view);

    }


}
