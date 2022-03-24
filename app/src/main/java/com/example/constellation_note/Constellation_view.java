package com.example.constellation_note;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Constellation_view extends FrameLayout implements Button.OnClickListener
{

    // 해당 별자리 뷰의 인덱스(순서)
    private int index;
    private int width = MainActivity.width / 3;
    private int height = MainActivity.height / 3;

    private Button button_confirm;
    private LinearLayout menu_layout;

    private ViewGroup.LayoutParams constellation_params;

    private MainActivity mainActivity = null;

    private List<Star> stars = new ArrayList<>();

    private Context context;

    public Constellation_view(@NonNull Context context, int index)
    {
        super(context);

        this.context = context;

        this.index = index;

        this.setBackgroundResource(R.drawable.constellation_border);

        constellation_params = new LayoutParams(width, height);

        this.setLayoutParams(constellation_params);

        // 초기 위치
        // 총 5개의 별자리를 생성하고 화면에는 총 3개의 별자리를 출력한다.
        // 나머지 2개의 별자리는 미리 로딩을 해놓는 것..
        // 0 ~ 4 까지의 인덱스가 있고, 2번 인덱스가 중앙에 오도록 하여야한다.

        float constellation_position = (MainActivity.width / 2) + (width * (index - 2) ) - width / 2;

        this.setX(constellation_position);
        this.setY(MainActivity.height / 2 - height / 2);

        menu_layout = new LinearLayout(getContext());

        LinearLayout.LayoutParams menu_param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        menu_layout.setLayoutParams(menu_param);

        menu_layout.setGravity(Gravity.RIGHT);
        menu_layout.setBackgroundResource(R.drawable.constellation_border);

        button_confirm = new Button(context);

        int button_width = width / 3;
        int button_height = height / 8;

        LinearLayout.LayoutParams button_param = new LinearLayout.LayoutParams(button_width, button_height);
        button_confirm.setLayoutParams(button_param);

        button_confirm.setText("확인");
        button_confirm.setOnClickListener(this);

        menu_layout.addView(button_confirm);
        this.addView(menu_layout);

        create_star(width / 2, height / 2);
    }



    public interface Callback_constellation
    {
        public void normal_mode(Constellation_view constellation_view);
    }

    public void setCallback_constellation(MainActivity activity)
    {
        this.mainActivity = activity;
    }

    public int getIndex()
    {
        return this.index;
    }

    public int get_width()
    {
        return this.width;
    }

    public int get_height()
    {
        return this.height;
    }

    public void set_width(int width)
    {
        this.width = width;
    }

    public void set_height(int height)
    {
        this.height = height;
    }

    public void set_size()
    {
        constellation_params.width = this.width;
        constellation_params.height = this.height;
    }

    public void setIndex(boolean direction)
    {

        if(direction)
        {

            if(index == 4)
            {
                index = 0;
            }
            else
            {
                index++;
            }

        }
        else
        {

            if(index == 0)
            {
                index = 4;
            }
            else
            {
                index--;
            }


        }

    }


    @Override
    public void onClick(View view)
    {

        if(mainActivity != null)
        {
            this.mainActivity.normal_mode(this);
        }

    }

    public void set_star_position()
    {
        System.out.println("별 위치 조정");
        Iterator<Star> iterator = stars.iterator();

        while(iterator.hasNext())
        {
            Star iter = iterator.next();
            iter.setX(this.get_width() / 2 - Star.getSize() / 2);
            iter.setY(this.get_height() / 2 - Star.getSize() / 2);
        }

    }

    public void long_click_star(View view)
    {

        if(mainActivity.isFocused)
        {
            // 터치 메소드가 있는 메인 액티비티에서 담당하는 것이 맞는 것 같다.
            // 따라서 메인 액티비티에서 터치 좌표를 얻는 것이 아니라
            // 메인 액티비티 자체에서 처리하도록 해야 한다.
            /*
            float touch_position[] = this.mainActivity.get_touch_position();
            create_star(touch_position[0], touch_position[1]);
            */

            this.mainActivity.popup_star_menu(view, this);

        }

    }

    public void create_star(float x, float y)
    {
        Star star = new Star(context, this);
        star.setIndex(stars.size());
        star.set_Postion(x, y);
        stars.add(star);
        this.addView(star);
        this.requestLayout();
    }

    public void remove_star(Star star)
    {
        stars.remove(star);
    }

    /*
    @Override
    public void click_star()
    {

    }
    */
}
