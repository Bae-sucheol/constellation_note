package com.example.constellation_note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.TextViewCompat;

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

    private int id;
    private String title;

    private Button button_confirm;
    private LinearLayout menu_layout;
    private EditText edit_title;

    private ViewGroup.LayoutParams constellation_params;

    private MainActivity mainActivity = null;

    private List<Star> stars = new ArrayList<>();

    private Context context;

    // sql 컨트롤 객체
    private SQLiteControl sqLiteControl;

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

        menu_layout.setBackgroundResource(R.drawable.constellation_border);

        button_confirm = new Button(context);

        int button_width = width / 3;
        int button_height = height / 8;

        LinearLayout.LayoutParams button_param = new LinearLayout.LayoutParams(button_width, button_height);
        button_param.weight = 1;
        button_confirm.setLayoutParams(button_param);

        button_confirm.setText("확인");
        button_confirm.setOnClickListener(this);

        edit_title = new EditText(getContext());

        LinearLayout.LayoutParams edit_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        edit_param.weight = 8;
        edit_title.setLayoutParams(edit_param);
        edit_title.setText("별자리 이름");
        edit_title.setTextColor(Color.WHITE);

        menu_layout.addView(edit_title);

        menu_layout.addView(button_confirm);
        this.addView(menu_layout);

        Handler handler = MainHandler.getMainHandler(context);

        sqLiteControl = new SQLiteControl(SQLiteHelper.getSqLiteHelper(getContext()), handler);
    }

    public interface Callback_constellation
    {
        public void normal_mode(Constellation_view constellation_view);
    }

    // setter

    public void set_id(int id)
    {
        this.id = id;
    }

    public void setTitle(String title)
    {
        this.title = title;
        edit_title.setText(title);
    }

    public void setCallback_constellation(MainActivity activity)
    {
        this.mainActivity = activity;
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

        int max_index = mainActivity.getConstellationSize() - 1;

        if(direction)
        {

            if(index == max_index)
            {

                // 4보다 크면. 즉 별자리가 5개 이상이면 새로운 정보를 불러오고 기존 별자리 정보는 삭제해야 한다.
                if(mainActivity.getMax_constellation_index() > 4)
                {
                    reset_constellation();
                }
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
                // 4보다 크면. 즉 별자리가 5개 이상이면 새로운 정보를 불러오고 기존 별자리 정보는 삭제해야 한다.
                if(mainActivity.getMax_constellation_index() > 4)
                {
                    reset_constellation();
                }
                index = max_index;
            }
            else
            {
                index--;
            }


        }

    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    // getter

    public int get_id()
    {
        return this.id;
    }

    public String getTitle()
    {
        return this.title;
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

            //iter.setX(this.get_width() / 2 - Star.getSize() / 2);
            //iter.setY(this.get_height() / 2 - Star.getSize() / 2);

            iter.set_Position_relative();
            iter.calculate_size();
            iter.setSize();
            iter.setTitle_view();

        }

        this.requestLayout();

    }

    // 모든 요소마다 조건을 검사하면 비효율적.
    // 최상단에서 한번만 조건을 파악하는 것이 효율적이라고 생각했다.
    // 선은 부모별을 가진 별이 존재할 때 즉 별자리에 별이 최소 2개일때 실행하는 것이 옳다.
    public void redraw_star_line()
    {
        if(stars.size() > 1)
        {
            Iterator<Star> iterator = stars.iterator();

            while(iterator.hasNext())
            {
                Star iter = iterator.next();

                iter.remove_line();
                iter.draw_line();

            }
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

    public void click_star(View view)
    {
        if(mainActivity.isFocused)
        {
            mainActivity.setUseStar((Star)view);

            Intent intent = new Intent(getContext(), Create_note.class);

            Bundle bundle = new Bundle();

            Star star = (Star)view;

            intent.putExtra("id", star.getIndex());
            intent.putExtra("constellation_id", this.id);
            intent.putExtra("title", star.getTitle());
            intent.putExtra("content", star.getContent());

            intent.putExtras(bundle);

            mainActivity.startActivityResult.launch(intent);
        }
    }

    public void create_star(float x, float y)
    {
        Star star = new Star(context, this);
        star.setIndex(stars.size());
        star.set_Postion(x, y);
        star.calculate_relative_position();
        star.setTitle("임시 제목");
        stars.add(star);
        this.addView(star);
        this.requestLayout();
    }

    public void remove_star(Star star)
    {
        stars.remove(star);
    }

    public Star get_last_star()
    {
        return stars.get(stars.size() - 1);
    }

    public MainActivity getMainActivity()
    {
       return this.mainActivity;
    }

    public void find_stars_parent()
    {
        Iterator<Star> iterator = stars.iterator();

        while(iterator.hasNext())
        {
            Star iter = iterator.next();

            Star parent = find_star(iter.getParent_index());

            iter.setParent(parent);
        }

        redraw_star_line();
    }

    private Star find_star(int id)
    {

        Iterator<Star> iterator = stars.iterator();

        Star star = null;

        while(iterator.hasNext())
        {
            Star iter = iterator.next();

            if(iter.getIndex() == id)
            {
                star = iter;

                break;
            }
        }

        return star;
    }

    private void reset_constellation()
    {

        Iterator<Star> iterator = stars.iterator();

        while(iterator.hasNext())
        {
            Star star = iterator.next();
            star.remove_line();
            this.removeView(star);
        }

        stars.clear();
        this.requestLayout();

    }

    /*
    @Override
    public void click_star()
    {

    }
    */
}
