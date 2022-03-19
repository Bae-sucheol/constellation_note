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

public class Constellation_view extends FrameLayout
{

    // 해당 별자리 뷰의 인덱스(순서)
    private int index;
    public static int width = MainActivity.width / 3;
    public static int height = MainActivity.height / 3;

    private Button button_confirm;
    private LinearLayout menu_layout;

    public Constellation_view(@NonNull Context context, int index)
    {
        super(context);

        this.index = index;

        this.setBackgroundResource(R.drawable.constellation_border);

        ViewGroup.LayoutParams constellation_params = new LayoutParams(width, height);

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

        button_confirm = new Button(getContext());

        int button_width = width / 3;
        int button_height = height / 8;

        LinearLayout.LayoutParams button_param = new LinearLayout.LayoutParams(button_width, button_height);
        button_confirm.setLayoutParams(button_param);

        button_confirm.setText("확인");

        menu_layout.addView(button_confirm);
        this.addView(menu_layout);


    }

    public int getIndex()
    {
        return this.index;
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


}
