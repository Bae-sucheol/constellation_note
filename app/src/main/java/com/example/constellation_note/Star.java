package com.example.constellation_note;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class Star extends View
{

    // 최상위 번호
    private static int max_index;

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

        this.setBackgroundResource(R.drawable.star_glow);

        LinearLayout.LayoutParams star_param = new LinearLayout.LayoutParams(size, size);

        this.setLayoutParams(star_param);

        //this.setX(MainActivity.width - size / 2);
        //this.setY(MainActivity.height - size / 2);

        this.setX(constellation.get_width() / 2 - size / 2);
        this.setY(constellation.get_height() / 2 - size / 2);
    }



}
