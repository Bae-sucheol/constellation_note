package com.example.constellation_note;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, Constellation_view.Callback_constellation
{

    private static final int NUM_STAR = 50; // 별 개수
    private static final int STAR_SIZE = 32; // 별 사이즈
    private static final int MAX_Z = 10; // 별과 스크린간의 최대거리.
    private static final int MIN_Z = 5; // 별과 스크린간의 최소거리.
    private static final float MAX_MAGNIFICATION = 0.4f; // 화면 너비에 맞춰 별자리 뷰의 사이즈를 정한다.(최대)
    private static final int SPEED_MULTIPLIER = 2; // 별이 움직이는 속도 배수
    private static final float DISTORTION = 0.2f; // 공간이 왜곡되는 정도
    private static final float SWIPE_MAGNIFUCATION = 0.4f; // 얼마나 스와이프 해야 화면이 넘어가는지(별자리 페이지가 넘어가는지)
    private static final int ANIMATION_TIME = 200; // 애니메이션 타임.
    private static final int CONSTELLATION_Z = 2; // 별자리 뷰와 스크린간의 거리 (움직이는 속도와도 관계가 있음)

    private static boolean temp_star_mode = false;

    public static boolean isFocused = false;

    public static int width;
    public static int height;

    private FrameLayout frameLayout_main;
 
    private float touch_pre_x; // 터치한 지점의 x좌표
    private float touch_move_distance; // 터치 시작 지점부터 터치를 끝낸 지점까지의 거리.
    private float touch_move_pre_x; // 터치 후 움직일 때 이전 좌표.
    private float touch_pre_y; // 터치한 지점의 y좌표


    private ImageView star_list[]; // 이미지 뷰 리스트 ( 별 )
    private int star_distance[]; // 별의 거리

    private float star_y[]; //

    private int animation_count;

    //private Constellation_view constellations[]; // 별자리 뷰 리스트
    private List<Constellation_view> constellations;

    private boolean isTouchConstellation = false; // 별자리를 터치했는지...

    private int bottom__bar_height;

    private Star temp_star;

    private ImageView imageView_add_constellation;

    private SQLiteHelper sqLiteHelper;
    private SQLiteControl sqLiteControl;

  @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 사이즈를 구한다.

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        width = size.x;
        height = size.y;

        // 하단 네이게이션바 사이즈
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            bottom__bar_height = resources.getDimensionPixelSize(resourceId);
        }

        // 부모 뷰(컨테이너)

        frameLayout_main = (FrameLayout)findViewById(R.id.frameLayout_main);

        // 터치 리스너
        frameLayout_main.setOnTouchListener(this);

        //
        sqLiteHelper = new SQLiteHelper(MainActivity.this, "constellation_note.db", null, 4);
        sqLiteControl = new SQLiteControl(sqLiteHelper);

        // 별 생성
        create_stars(NUM_STAR);

        // 별자리 생성
        //constellations = new Constellation_view[5];
        constellations = new ArrayList<>();

        //create_constellations();

        imageView_add_constellation = findViewById(R.id.imageView_add_constellation);

    }

    private void create_stars(int num_star)
    {

        star_list = new ImageView[num_star];
        star_distance = new int[num_star];
        star_y = new float[num_star];

        // 별 컬러 리스트
        int star_colors[] = {R.color.star_blue , R.color.star_white_blue, R.color.star_white, R.color.star_yellow, R.color.star_orange, R.color.star_red};

        for (int i = 0; i < star_list.length; i++)
        {

            star_list[i] = new ImageView(this);
            star_list[i].setImageDrawable(this.getResources().getDrawable(R.drawable.star_glow));

            // 좌표(위치) 설정
            Random random = new Random();
            //random.setSeed() - 시드값을 고정시키면 같은 결과.

            float random_x = random.nextFloat() * (width - STAR_SIZE);
            float random_y = random.nextFloat() * (height - STAR_SIZE);
            int random_z = random.nextInt(MAX_Z - (MIN_Z - 1)) + MIN_Z;
            int random_star_color = random.nextInt(5);

            star_distance[i] = random_z;
            star_y[i] = random_y;
            star_list[i].setX(random_x);
            star_list[i].setY(random_y);

            star_list[i].setColorFilter(getResources().getColor(star_colors[random_star_color]));

            int star_dia = (int)(STAR_SIZE - Math.pow(random_z, 1.3));

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(star_dia, star_dia);
            star_list[i].setLayoutParams(lp);

            // 부모에 추가
            frameLayout_main.addView(star_list[i]);
        }
        // 별의 초기 위치를 초기화 하기 위해 실행한다.
        // 안하면 첫 이동시 위치가 순간적으로 변함.
        // 결국 문제 파악을 못해서 그냥 move 함수를 한번 적용해 주기로 했음.
        move_stars(0, 0, 10);

    }

    private void create_constellations()
    {
        /*
        constellations = new Constellation_view[5];

        for(int i = 0; i < 5; i++)
        {
            Constellation_view constellation = new Constellation_view(this, i);
            constellation.setOnTouchListener(this);
            constellations[i] = constellation;
            constellations[i].setCallback_constellation(this);
            frameLayout_main.addView(constellation);
        }
         */
        Constellation_view constellation = new Constellation_view(this, constellations.size());
        constellation.setCallback_constellation(this);
        constellation.setOnTouchListener(this);
        constellations.add(constellation);
        frameLayout_main.addView(constellation);

        // sql 삽입
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", "별자리 이름");

        sqLiteControl.insert(sqLiteHelper.getTable_constellation(), contentValues);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {

        if(isFocused)
        {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN :
                    System.out.println("움직여라1");
                    touch_pre_x = motionEvent.getX();
                    touch_pre_y = motionEvent.getY();

                    break;
                case MotionEvent.ACTION_UP :

                    //isTouchConstellation = false;

                    if(temp_star_mode)
                    {
                        temp_star.calculate_relative_position();
                        temp_star.setAlpha(1.0f);
                        temp_star.draw_line();
                        temp_star_mode = false;
                    }

                    break;

                case MotionEvent.ACTION_MOVE :

                    if(temp_star_mode)
                    {
                        temp_star.set_Postion(motionEvent.getX(), motionEvent.getY());
                    }

                    break;
                default :
                    break;
            }

            //return false;
        }
        else
        {

            if(view instanceof Constellation_view)
            {
                isTouchConstellation = true;
            }

            switch (motionEvent.getAction())
            {
                // 화면을 터치할 때.
                case MotionEvent.ACTION_DOWN:

                    touch_pre_x = motionEvent.getX();
                    touch_move_pre_x = touch_pre_x;

                    break;
                // 화면 터치가 종료될 때. 즉 화면에서 손을 뗄 때.
                case MotionEvent.ACTION_UP:

                    float current_x = motionEvent.getX();

                    if(isTouchConstellation && touch_pre_x == current_x)
                    {
                        isTouchConstellation = false;
                        creative_mode((Constellation_view)view);
                    }

                    if(constellations.size() < 4)
                    {
                        break;
                    }

                    touch_move_distance = current_x - touch_pre_x;

                    System.out.println("거리 : " + touch_move_distance);
                    System.out.println("거리 절대값 : " + Math.abs(touch_move_distance));

                    // 일정 범위 이상 스와이프 했을경우 다음 페이지(별자리)로 넘어간다.
                    // 좌측으로 스와이프 할 때 우측으로 스와이프 할 때가 음수 양수로 값이 다르다.
                    // 따라서 경우를 나누어 주어야 한다.

                    if(Math.abs(touch_move_distance) >= width * SWIPE_MAGNIFUCATION)
                    {
                        // 별자리의 최대 사이즈 만큼 움직여야 하기 때문에
                        // width * MAX_MAGNIFICATION 만큼 움직여야 하기 때문에
                        // 터치를 종료한 지점부터 터치를 시작한 지점 + width * MAX_MAGNIFICATION 까지

                        float page_deviation;

                        // 음수 양수를 통해 좌측 이동인지 우측 이동인지 판별
                        if(touch_move_distance > 0)
                        {

                            set_constellation_index(true);

                            // 한 페이지 만큼을 움직여야 한다.
                            page_deviation = current_x + (width - touch_move_distance);

                        }
                        else
                        {

                            set_constellation_index(false);

                            page_deviation = current_x - (width + touch_move_distance);

                        }

                        move_stars(current_x, page_deviation, 10);

                    }
                    else
                    {
                        // 반대로 일정 범위 이상 스와이프를 하지 못했을 경우
                        // 원래 자리로 돌아가야 한다.
                        // 터치를 종료한 지점 ~ 터치를 시작한 지점까지의 거리를 구해서
                        // 자연스럽게 원래 자리로 이동 시켜야 한다.
                        ;
                        move_stars(current_x, touch_pre_x, 10);
                    }

                    break;
                // 터치중에 움직일 때.
                case MotionEvent.ACTION_MOVE:

                    if(constellations.size() < 4)
                    {
                        break;
                    }

                    if(touch_move_pre_x != motionEvent.getX())
                    {

                        move_stars(touch_move_pre_x, motionEvent.getX());
                        touch_move_pre_x = motionEvent.getX();

                    }

                    break;

                default:

                    break;

            }
        }

        return true;
    }

    // 일단은 move_stars로 두고 나중에 적절한 메소드명을 지어주기로 했다.
    // 현재는 stars, constellations 를 전부 이동시킨다.
    // runOnUiThread 를 동시에 사용하니 문제가 생겨 하나로 통합하기로 했다.
    private void move_stars(float pre_x, float post_x) {
        // 페이지당 움직여야할 총 거리를 정하고
        // 선형보간 방정식으로 움직여야할 것 같다.

        // 선형보간.

        for (int i = 0; i < star_list.length; i++)
        {

            float current_x = star_list[i].getX();
            float current_y = star_list[i].getY();

            float delta_distance = pre_x - post_x;

            // Y축 왜곡을 만들어줘야한다.

            int y_center = height / 2;

            float y_gap = (y_center - current_y) / y_center; // 0 ~ 1의 값을 갖는다.

            // 위치별 각도(degree) 0 ~ 180 사이의 값을 갖는다.
            double position_per_degree = current_x / width * 180;

            // 거리별로 움직이는 속도가 다르다
            star_list[i].setX(current_x - delta_distance * SPEED_MULTIPLIER / star_distance[i]);

            star_list[i].setY(star_y[i] + (float)Math.sin(Math.toRadians(position_per_degree)) * y_gap * (y_center * DISTORTION) );

           // 화면을 넘어가면 반대쪽으로 이동.

            // 요소의 왼쪽 상단을 기준으로 좌표가 시작되기 때문에 0보다 작은경우가 맞다.
            if (current_x < 0)
            {
                // 요소의 왼쪽 상단을 기준으로 좌표가 시작되므로
                // 별의 사이즈만큼 빼주어야 맨 오른쪽으로 자연스럽게 이동한다.
                star_list[i].setX(width - star_distance[i]);
            }

            // 역시 마찬가지로 화면이 넘어가면 반대쪽으로 이동.
            if (current_x > width - star_distance[i])
            {
                star_list[i].setX(0);
            }

            if (current_y < 0)
            {
                star_list[i].setY(height - star_distance[i]);
            }

            if (current_y > height - star_distance[i])
            {
                star_list[i].setY(0);
            }

        }

        // constellation 파트
        Iterator<Constellation_view> iter = constellations.iterator();

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            float current_x = constellation.getX();
            float delta_distance = pre_x - post_x;

            constellation.setX(current_x - delta_distance / 3);

            float center_x = (width / 2) - constellation.get_width() / 2;
            float normalization = (center_x - current_x) / (width / 2);
            normalization = Math.abs(normalization);
            float interpolation = (1.0f - normalization) + (0.2f * normalization);
            System.out.println("normalization : " + normalization);
            constellation.setAlpha(interpolation);
        }

    }

    private void move_stars(float pre_x, float post_x, int interval)
    {

        animation_count = 0;

        int total_count = ANIMATION_TIME / interval;

        //float delta_distance = (pre_x - post_x) / total_count;
        float delta_distance = (post_x - pre_x) / total_count;

        System.out.println((post_x - pre_x));

        // 부드러운 이동을 위해 타이머를 이용하여 목표 지점까지 등속도로 움직인다.
        // 이때 UI 쓰레드에서 작동할 수 있도록 runOnUiThread 메소드를 이용한다.
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {

            public void run(){

                runOnUiThread(new Runnable()
                {

                    public void run()
                    {

                        float move_pre_x = pre_x + (animation_count * delta_distance);

                        move_stars(move_pre_x, move_pre_x + delta_distance);
                        animation_count++;

                        if(animation_count == total_count)
                        {
                            timer.cancel();
                            // float 소수점 편차로 인해서 약간의 보정이 필요하다.
                            // move_stars 함수를 이용해서 움직이려고 했으나 터치 좌표가 기준이며, 다른 계산식이 섞여있어
                            // 그냥 따로 위치를 재배치 하기로 했다.

                            set_constellation_position();
                            move_stars(0.0f, 0.0f);
                        }

                    }

                });

            }
        };

        timer.schedule(timerTask, 0, 10);

    }

    private void set_constellation_index(boolean direction)
    {

        Iterator<Constellation_view> iter = constellations.iterator();

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            constellation.setIndex(direction);
        }

    }

    private void set_constellation_position()
    {

        Iterator<Constellation_view> iter = constellations.iterator();

        int center_index = (constellations.size() - 1) / 2;

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            float constellation_position = (width / 2) + (constellation.get_width() * (constellation.getIndex() - center_index) ) - constellation.get_width() / 2;
            constellation.setX( (int)(constellation_position) );
        }

    }


    private void creative_mode(Constellation_view view)
    {

        // 전체화면
        // 추후 보간식을 이용해서 애니메이션 처리 할 예정

        int delta_index = 2 - view.getIndex();

        Iterator<Constellation_view> iter = constellations.iterator();

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            if(delta_index == 1)
            {
                constellation.setIndex(true);
            }
            else if(delta_index == -1)
            {
                constellation.setIndex(false);
            }

            if(constellation == view)
            {
                constellation.setAlpha(1.0f);

                constellation.setX(0.0f);
                constellation.setY(0.0f);

                constellation.set_width(width);
                constellation.set_height(height - bottom__bar_height);
                constellation.set_size();
            }
            else
            {
                constellation.setVisibility(View.GONE);
            }

        }

        imageView_add_constellation.setVisibility(View.GONE);
        view.set_star_position();
        view.redraw_star_line();
        isFocused = true;

    }

    public void normal_mode(Constellation_view view)
    {

        view.set_width(width / 3);
        view.set_height(height / 3);
        view.set_size();

        set_constellation_position();

        Iterator<Constellation_view> iter = constellations.iterator();

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            constellation.setVisibility(View.VISIBLE);
        }

        imageView_add_constellation.setVisibility(View.VISIBLE);
        view.requestLayout();
        view.setY(height / 2 - view.get_height() / 2);
        view.set_star_position();
        view.redraw_star_line();
        isFocused = false;
        move_stars(0, 0);

    }

    /*
    public float[] get_touch_position()
    {

        float touch_position[] = new float[2];

        touch_position[0] = touch_pre_x;
        touch_position[1] = touch_pre_y;

        return touch_position;
    }
     */

    /*
    public void temp_star(Constellation_view constellation)
    {
        temp_star = new Star(this, constellation);

        constellation.requestLayout();

        temp_star.set_Postion(touch_pre_x, touch_pre_y);

        constellation.addView(temp_star);

        temp_star.setAlpha(0.5f);
        temp_star_mode = true;
    }
     */

    public void popup_star_menu(View view, Constellation_view constellation)
    {
        Star star = (Star)view;

        final android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.action_star_create :

                        //touch_pre_x = star.get_x();
                        //touch_pre_y = star.get_y();
                        //temp_star(constellation);
                        constellation.create_star(star.get_x(), star.get_y());
                        temp_star = constellation.get_last_star();
                        temp_star.setAlpha(0.5f);
                        temp_star.setParent(star);
                        temp_star_mode = true;

                        break;
                    case R.id.action_star_delete :

                        // 별자리의 arraylist 에서 삭제
                        constellation.remove_star(star);
                        // 별자리의 뷰에서 삭제
                        constellation.removeView(star);
                        // 별과 별을 잇는(이어주는) 선을 지운다.
                        star.remove_line();

                        break;
                    case R.id.action_star_modify :

                        star.remove_line();
                        temp_star = star;
                        temp_star.setAlpha(0.5f);
                        temp_star_mode = true;

                        break;
                    default:

                        break;
                }

                return false;
            }
        });
        popupMenu.show();
    }

    public void onClickCreateConstellation(View view)
    {
        create_constellations();
        set_constellation_position();
    }

    public int getConstellationSize()
    {
        return constellations.size();
    }

    public SQLiteHelper getSqLiteHelper()
    {
        return this.sqLiteHelper;
    }

    public SQLiteControl getSqLiteControl()
    {
        return this.sqLiteControl;
    }
    
}