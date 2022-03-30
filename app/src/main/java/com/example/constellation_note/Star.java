package com.example.constellation_note;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class Star extends View implements View.OnLongClickListener
{

    // 별의 번호
    private int index;

    // 별들의 사이즈.
    private static int size;

    // 레이아웃 파라미터
    ViewGroup.LayoutParams star_param;

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

    public Star(Context context, Constellation_view constellation)
    {
        super(context);

        this.constellation = constellation;

        this.calculate_size();

        this.setBackgroundResource(R.drawable.star_glow);

        star_param = new ViewGroup.LayoutParams(size, size);

        this.setLayoutParams(star_param);

        this.setOnLongClickListener(this);
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

    public void remove_line()
    {
        constellation.removeView(line);
        line = null;
    }

    public void calculate_relative_position()
    {
        this.relative_x = this.getX() / (float)constellation.get_width();
        this.relative_y = this.getY() / (float)constellation.get_height();
    }

    public void set_Position_relative()
    {
        System.out.println("별자리 절대 x : " + this.relative_x);
        System.out.println("별자리 절대 y : " + this.relative_y);
        this.setX(constellation.get_width() * this.relative_x);
        this.setY(constellation.get_height() * this.relative_y);
    }

    public void draw_line()
    {
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

    @Override
    public boolean onLongClick(View view)
    {

        constellation.long_click_star(view);

        return true;

    }

}
