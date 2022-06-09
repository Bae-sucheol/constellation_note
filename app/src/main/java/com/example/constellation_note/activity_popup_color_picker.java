package com.example.constellation_note;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class activity_popup_color_picker extends Activity implements View.OnClickListener {

    private int color_id;

    private int ColorList[] = {R.color.star_blue , R.color.star_white_blue, R.color.star_white, R.color.star_yellow, R.color.star_orange, R.color.star_red};

    private LinearLayout color1;
    private LinearLayout color2;
    private LinearLayout color3;
    private LinearLayout color4;
    private LinearLayout color5;
    private LinearLayout color6;

    private LinearLayout selected_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_color_picker);

        // 따로 추가하려고 했으나 설정이 잘 적용되지 않기도 하고
        // 색상도 많지 않아서 그냥 따로 작성하기로 했음.

        color1 = findViewById(R.id.layout_color_1);
        color2 = findViewById(R.id.layout_color_2);
        color3 = findViewById(R.id.layout_color_3);
        color4 = findViewById(R.id.layout_color_4);
        color5 = findViewById(R.id.layout_color_5);
        color6 = findViewById(R.id.layout_color_6);

        color1.setOnClickListener(this);
        color2.setOnClickListener(this);
        color3.setOnClickListener(this);
        color4.setOnClickListener(this);
        color5.setOnClickListener(this);
        color6.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {

        if(selected_layout != null)
        {
            selected_layout.setBackground(getDrawable(R.drawable.color_unselected));
        }

        switch (view.getId())
        {
            case R.id.layout_color_1:

                color1.setBackground(getDrawable(R.drawable.color_selected));
                selected_layout = color1;
                color_id = ColorList[0];

                break;
            case R.id.layout_color_2:

                color2.setBackground(getDrawable(R.drawable.color_selected));
                selected_layout = color2;
                color_id = ColorList[1];

                break;
            case R.id.layout_color_3:

                color3.setBackground(getDrawable(R.drawable.color_selected));
                selected_layout = color3;
                color_id = ColorList[2];

                break;
            case R.id.layout_color_4:

                color4.setBackground(getDrawable(R.drawable.color_selected));
                selected_layout = color4;
                color_id = ColorList[3];

                break;
            case R.id.layout_color_5:

                color5.setBackground(getDrawable(R.drawable.color_selected));
                selected_layout = color5;
                color_id = ColorList[4];

                break;
            case R.id.layout_color_6:

                color6.setBackground(getDrawable(R.drawable.color_selected));
                selected_layout = color6;
                color_id = ColorList[5];

                break;
        }

    }

    public void onClickColorPick(View view)
    {
        finish_activity();
    }

    private void finish_activity()
    {
        Intent intent = new Intent();
        intent.putExtra("requestCode", 2);
        intent.putExtra("color", color_id);
        setResult(RESULT_OK, intent);
        finish();
    }

}