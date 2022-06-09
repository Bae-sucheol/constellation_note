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
import android.widget.LinearLayout;
import android.widget.TextView;

public class activity_popup_color_picker extends Activity implements View.OnClickListener {

    private int color_id;

    private GridLayout gridLayout;
    private Button confirm;

    private int ColorList[] = {R.color.star_blue , R.color.star_white_blue, R.color.star_white, R.color.star_yellow, R.color.star_orange, R.color.star_red};
    private TextView views[];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_color_picker);

    }

    private void finish_activity()
    {
        Intent intent = new Intent();
        intent.putExtra("requestCode", 2);
        intent.putExtra("color", color_id);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onClick(View view)
    {



    }
}