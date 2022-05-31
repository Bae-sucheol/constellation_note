package com.example.constellation_note;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public static final int GET_LAST_CONSTELLATION_ID = 1;
    public static final int GET_CONSTELLATION_LIST = 2;
    public static final int GET_CONSTELLATION_SINGLE = 3;
    public static final int GET_STARS_LIST = 4;

    private static boolean temp_star_mode = false;
    private static boolean modify_star_mode = false;

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

    //sql 컨트롤 객체
    private SQLiteControl sqLiteControl;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    //private static Handler handler = new Handler();
    //private Handler handler = new MainThreadHandler(this);
    private Handler handler = new MainHandler(this);

    private int swap_target = 0;

    private int max_constellation_index = -1;

    private boolean isLongclick = false;

    private Star useStar;

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

        // 데이터베이스(SQLITE) 싱글톤 객체 초기화

        sqLiteControl = new SQLiteControl(SQLiteHelper.getSqLiteHelper(this), handler);

        // 별 생성
        create_stars(NUM_STAR);

        // 별자리 생성
        //constellations = new Constellation_view[5];
        constellations = new ArrayList<>();

        //create_constellations();

        imageView_add_constellation = findViewById(R.id.imageView_add_constellation);

        //(String table, String columns[], String selection, String selectionArgs[], String orderBy)
        //sqLiteControl.select(sqLiteControl.getTable_constellation(), new String[] {"id"}, null, null, GET_LAST_CONSTELLATION_ID);
        //String table, String columns[], String selection, String selectionArgs[], int flag)

        SQLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_SELECT, sqLiteControl.getTable_constellation(), new String[] {"id"}, GET_LAST_CONSTELLATION_ID));
        submitRunnable(sqLiteControl);

    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        //처리
                       Intent intent = result.getData();

                        useStar.setTitle(intent.getStringExtra("title"));
                        useStar.setContent(intent.getStringExtra("content"));
                       
                    }
                }
            }
    );

    public void create_stars(int num_star)
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

    public void create_constellations()
    {

        Constellation_view constellation = new Constellation_view(this, constellations.size());
        constellation.setCallback_constellation(this);
        constellation.setOnTouchListener(this);
        constellation.set_id(max_constellation_index);
        constellation.create_star(constellation.get_width() / 2, constellation.get_height() / 2);
        constellation.get_last_star().setTitle("임시 제목");

        constellations.add(constellation);
        frameLayout_main.addView(constellation);

        // sql 삽입
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", constellation.get_id());
        contentValues.put("title", "별자리 이름");

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_INSERT, sqLiteControl.getTable_constellation(), contentValues));
        submitRunnable(sqLiteControl);

        // 에러 발생 위치. 일단은 다른 작업부터 먼저 하고 나중에 처리.
        constellation.get_last_star().insert_into_star();
    }

    public void create_constellation(Constellation_data constellation_data)
    {

        Constellation_view constellation = new Constellation_view(this, constellations.size());
        constellation.set_id(constellation_data.getId());
        constellation.setTitle(Integer.toString(constellation_data.getId()));
        constellation.setCallback_constellation(this);
        constellation.setOnTouchListener(this);
        constellations.add(constellation);
        frameLayout_main.addView(constellation);

        request_stars_data(constellation_data.getId());
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {

        if(isFocused)
        {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN :

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
                        temp_star.insert_into_star();
                        temp_star.setTitle("임시 제목");
                        temp_star_mode = false;
                    }

                    if(modify_star_mode)
                    {
                        temp_star.calculate_relative_position();
                        temp_star.setAlpha(1.0f);
                        temp_star.draw_line();
                        temp_star.update_star();
                        modify_star_mode = false;
                    }

                    break;

                case MotionEvent.ACTION_MOVE :

                    if(temp_star_mode || modify_star_mode)
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
            Timer long_click_timer = null;
            TimerTask long_click_timerTask = null;

            if(view instanceof Constellation_view)
            {
                isTouchConstellation = true;
                long_click_timer = new Timer();

                long_click_timerTask = new TimerTask()
                {

                    public void run()
                    {

                        runOnUiThread(new Runnable()
                        {

                            public void run()
                            {

                                if(touch_pre_x == motionEvent.getX() && touch_pre_y == motionEvent.getY() && isTouchConstellation)
                                {
                                    isLongclick = true;
                                    popup_constellation_menu(view);
                                }

                            }

                        });

                    }
                };

            }

            switch (motionEvent.getAction())
            {
                // 화면을 터치할 때.
                case MotionEvent.ACTION_DOWN:

                    touch_pre_x = motionEvent.getX();
                    touch_pre_y = motionEvent.getY();
                    touch_move_pre_x = touch_pre_x;

                    if(view instanceof Constellation_view)
                    {
                        isLongclick = false;
                        long_click_timer.schedule(long_click_timerTask, 1000); // 1초 후
                    }

                    break;
                // 화면 터치가 종료될 때. 즉 화면에서 손을 뗄 때.
                case MotionEvent.ACTION_UP:

                    float current_x = motionEvent.getX();

                    if(isTouchConstellation && touch_pre_x == current_x && isLongclick == false)
                    {
                        isTouchConstellation = false;
                        if(view instanceof Constellation_view)
                        {
                            creative_mode((Constellation_view)view);
                        }
                    }

                    if(constellations.size() < 4)
                    {
                        break;
                    }

                    touch_move_distance = current_x - touch_pre_x;

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
                            request_constellation_data(0);

                            // 한 페이지 만큼을 움직여야 한다.
                            page_deviation = current_x + (width - touch_move_distance);

                        }
                        else
                        {

                            set_constellation_index(false);
                            request_constellation_data(1);

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

            constellation.setAlpha(interpolation);
        }

    }

    private void move_stars(float pre_x, float post_x, int interval)
    {

        animation_count = 0;

        int total_count = ANIMATION_TIME / interval;

        //float delta_distance = (pre_x - post_x) / total_count;
        float delta_distance = (post_x - pre_x) / total_count;

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


    // 작업 쓰레드에 정보를 요청해서 받으면 처리를 하는거라
    // 어느 별자리에서 요청했는지 나중에 선별해서 넣어줘야한다.
    // 어차피 보이는 뷰3개 안보이는뷰 2개로 최대 5개이기 때문에
    // 탐색 비용은 적을 것 같다.
    private void request_stars_data(int constellation_id)
    {

        String selection = "constellation_id = ?";
        String selectionArgs[] = new String[1];

        selectionArgs[0] = Integer.toString(constellation_id);

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_SELECT, sqLiteControl.getTable_note(), new String[] {"*"}, selection, selectionArgs, GET_STARS_LIST));
        submitRunnable(sqLiteControl);
    }

    public void set_stars_data(ArrayList<Star_data> stars)
    {
        // 만약 반환된 별자리 내의 별이 없다면 작업을 취소.
        if(stars.size() == 0)
        {
            return;
        }

        int constellation_id = stars.get(0).getConstellation_id();

        Iterator<Constellation_view> constellation_iterator = constellations.iterator();

        while(constellation_iterator.hasNext())
        {
            Constellation_view constellation_view = constellation_iterator.next();

            if(constellation_id == constellation_view.get_id())
            {
                Iterator<Star_data> star_data_iterator = stars.iterator();

                while(star_data_iterator.hasNext())
                {

                    Star_data star_data = star_data_iterator.next();
                    // 여기서 별을만드는 동작을 구현해야함.
                    // 오늘은 여기까지
                    // constellation_view.create_star();
                    constellation_view.create_star(0.0f, 0.0f);
                    temp_star = constellation_view.get_last_star();
                    temp_star.setRelative_x(star_data.getRelative_x());
                    temp_star.setRelative_y(star_data.getRelative_y());
                    temp_star.set_Position_relative();
                    temp_star.setParent_index(star_data.getParent_index());
                    temp_star.setTitle(star_data.getTitle());
                    temp_star.setContent(star_data.getContent());
                    temp_star.setIndex(star_data.get_id());

                }

                constellation_view.find_stars_parent();

                break;
            }
        }

    }

    private void request_constellation_data(int direction)
    {
        // true 가 들어오면 1번째 인덱스의 id 값을 가져와서
        // 0번째(첫째)인덱스에 가져온 id의 별자리의 전 별자리 값들을 가져와서 덮어 씌운다.


        // false 가 들어오면 3번째 인덱스의 id값을 가져와서
        // 4번째(마지막)인덱스에 가져온 id의 별자리에 다음 별자리 값들을 가져와서 덮어 씌운다.

        String selection = "id = ?";
        String selectionArgs[] = new String[1];
        int target = 1 + (2 * direction);
        swap_target = (4 * direction);

        Iterator<Constellation_view> iter = constellations.iterator();

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            if(constellation.getIndex() == target)
            {

                int constellation_id = constellation.get_id();
                int next_id = constellation_id + (2 * direction - 1);

                // 다음 id가 1보다 작으면 즉 0이면 마지막 별자리를 불러와야한다.
                if(next_id < 0)
                {
                    selectionArgs[0] = Integer.toString(max_constellation_index);
                }
                else if(next_id > max_constellation_index) // 다음 id가 마지막 별자리 id보다 크면 첫번째 별자리를 불러와야한다.
                {
                    selectionArgs[0] = Integer.toString(0);
                }
                else
                {
                    selectionArgs[0] = Integer.toString(next_id);
                }

                break;
            }
        }

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_SELECT, sqLiteControl.getTable_constellation(), new String[] {"id", "title"}, selection, selectionArgs, GET_CONSTELLATION_SINGLE));
        submitRunnable(sqLiteControl);

    }

    public void swap_constellation_data(Constellation_data constellation_data)
    {

        //swap_target 을 이용한다.

        Iterator<Constellation_view> iter = constellations.iterator();

        while(iter.hasNext())
        {
            Constellation_view constellation = iter.next();

            if(constellation.getIndex() == swap_target)
            {
                constellation.set_id(constellation_data.getId());
                constellation.setTitle(Integer.toString(constellation_data.getId()));
                constellation.requestLayout();

                break;
            }
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

    public void popup_constellation_menu(View view)
    {

        Constellation_view constellation_view = (Constellation_view)view;

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        dialog.setTitle("삭제 메시지");
        dialog.setMessage("선택한 별자리를 삭제하시겠습니까? 별자리 내의 모든 기록이 같이 삭제됩니다." + constellation_view.get_id());

        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                /*
                    1. 별자리 내의 모든 별들을 삭제.
                    2. 별자리 삭제
                    3. 별자리 테이블의 모든 별자리 id를 적절하게 수정
                    4. autoincrement 수정
                 */

                String target_id = Integer.toString(constellation_view.get_id());

                // 1. 별자리 내의 모든 별들을 삭제
                //sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_DELETE, sqLiteControl.getTable_note(), "constellation_id = ?", new String[] {target_id}));
                //submitRunnable(sqLiteControl);

                // 아마 실행 후 바로 아래 구문이 실행되어 별자리만 삭제하는 것 같으니 중간에 작업이 끝났을 때 다시 실행하도록 바꿔야 한다.

                // 2. 별자리 삭제
                sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_DELETE, sqLiteControl.getTable_constellation(), "id = ?", new String[] {target_id}));
                submitRunnable(sqLiteControl);

                constellations.remove(constellation_view);
                frameLayout_main.removeView(constellation_view);

                // 별자리 테이블의 모든 별자리 id를 적절하게 수정
                sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_constellation(), "id > " + target_id, true));
                submitRunnable(sqLiteControl);

                // update cascade가 적용되지 않아 따로 처리를 또 해주어야함.
                // 해당 별자리에 포함된 별들의 constellation_id를 다 낮춰줘야함.. sqlite 거지같음.

                //sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_note(), "constellation_id > " + target_id, true));
                //submitRunnable(sqLiteControl);

                // 가독성을 위해 따로 증감문.
                max_constellation_index--;

                // autoincrement 수정
                //sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_ALTER, Integer.toString(max_constellation_index)));
                //submitRunnable(sqLiteControl);

                // 삭제되었으니 인덱스를 재정렬 해주어야한다.


                Iterator<Constellation_view> iter = constellations.iterator();

                while(iter.hasNext())
                {
                    Constellation_view constellation = iter.next();

                    if(constellation.getIndex() > constellation_view.get_id())
                    {
                        constellation.setIndex(constellation.getIndex() - 1);
                    }

                }



                // 인덱스 재정렬이 끝나면 다시 위치를 재조정
                set_constellation_position();
            }
        });
        dialog.show();
    }

    public void popup_star_menu(View view, Constellation_view constellation)
    {
        Star star = (Star)view;

        final android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.popup_star_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.action_star_create :

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
                        modify_star_mode = true;

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
        max_constellation_index++;
        create_constellations();
        set_constellation_position();
    }

    public int getConstellationSize()
    {
        return constellations.size();
    }

    public static void submitRunnable(Runnable runnable)
    {
        //executorService.submit(runnable);
        executorService.execute(runnable);
    }

    public void select_ConstellationData(int last_id)
    {

        max_constellation_index = last_id;

        String selection;

        String selectionArgs[];

        if(last_id > 4)
        {
            // for 문 쓰는 것 보다 이게 더 간편함.
            selection = "id IN(?, ?, ?, ?, ?)";
            selectionArgs = new String[5];

            selectionArgs[0] = Integer.toString(1);
            selectionArgs[1] = Integer.toString(2);
            selectionArgs[2] = Integer.toString(last_id - 2);
            selectionArgs[3] = Integer.toString(last_id - 1);
            selectionArgs[4] = Integer.toString(last_id);
        }
        else
        {

            StringBuffer stringBuffer = new StringBuffer("id IN(");
            selectionArgs = new String[last_id + 1];

            for(int i = 0; i <= last_id; i++)
            {
                selectionArgs[i] = Integer.toString(i);
                stringBuffer.append("?,");
            }

            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            stringBuffer.append(")");
            selection = stringBuffer.toString();

        }


        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_SELECT, sqLiteControl.getTable_constellation(), new String[] {"id", "title"}, selection, selectionArgs, GET_CONSTELLATION_LIST));
        submitRunnable(sqLiteControl);

    }

    public void setUseStar(Star star)
    {
        useStar = star;
    }
}