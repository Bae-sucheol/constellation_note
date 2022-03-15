package com.example.constellation_note;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class Constellation_view extends FrameLayout
{

    // 해당 별자리 뷰의 인덱스(순서)
    private int index;

    public Constellation_view(@NonNull Context context, int index)
    {
        super(context);

        this.index = index;

        this.setBackgroundResource(R.drawable.constellation_border);

        // 초기 사이즈
        int constellation_width = MainActivity.width / 3;
        int constellation_height = MainActivity.height / 3;

        ViewGroup.LayoutParams constellation_params = new LayoutParams(constellation_width, constellation_height);

        this.setLayoutParams(constellation_params);

        // 초기 위치
        // 총 5개의 별자리를 생성하고 화면에는 총 3개의 별자리를 출력한다.
        // 나머지 2개의 별자리는 미리 로딩을 해놓는 것..
        // 0 ~ 4 까지의 인덱스가 있고, 2번 인덱스가 중앙에 오도록 하여야한다.

        float constellation_position = (MainActivity.width / 2) + (constellation_width * (index - 2) ) - constellation_width / 2;

        this.setX(constellation_position);
        this.setY(MainActivity.height / 2 - constellation_height / 2);


    }

    public void move_constellations(float pre_x, float post_x)
    {



    }


}
