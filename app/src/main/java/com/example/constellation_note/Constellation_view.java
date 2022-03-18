package com.example.constellation_note;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class Constellation_view extends FrameLayout
{

    // 해당 별자리 뷰의 인덱스(순서)
    private int index;
    public static int width = MainActivity.width / 3;
    public static int height = MainActivity.height / 3;

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
