package com.example.constellation_note;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class Star extends View implements View.OnLongClickListener
{

    // 별의 번호
    private int index;

    // 별들의 사이즈.
    // 사이즈는 동일하게 할 것이기 때문에 클래스 변수로 정의
    private static int size = MainActivity.width / 20;

    // 별의 위치
    // 별의 위치는 각자 다르기 때문에.
    // 절대 위치가 아니라 상대 위치로 알아야 하기 때문에 비율(소숫점)로 정해야한다.
    private float x;
    private float y;

    // 부모만 알면 된다.
    // 첫 로딩 시. 번호가 낮은(루트 노드와 가까운) 순서대로 불러올 예정이기 때문에...
    private int parent_index;
    private Star Parent;

    private Constellation_view constellation;

    public Star(Context context, Constellation_view constellation)
    {
        super(context);

        this.constellation = constellation;

        this.setBackgroundResource(R.drawable.star_glow);

        LinearLayout.LayoutParams star_param = new LinearLayout.LayoutParams(size, size);

        this.setLayoutParams(star_param);

        this.setOnLongClickListener(this);
    }

    public interface Callback_star
    {
        public void click_star();
    }

    public static int getSize()
    {
        return size;
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

    @Override
    public boolean onLongClick(View view)
    {

        constellation.long_click_star(view);

        return true;

    }

}
