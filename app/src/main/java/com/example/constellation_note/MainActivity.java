package com.example.constellation_note;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener
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

    private int width;
    private int height;

    private FrameLayout frameLayout_main;

    private float touch_pre_x; // 터치한 지점의 x좌표
    private float touch_move_distance; // 터치 시작 지점부터 터치를 끝낸 지점까지의 거리.
    private float touch_move_pre_x; // 터치 후 움직일 때 이전 좌표.

    private ImageView star_list[]; // 이미지 뷰 리스트 ( 별 )
    private int star_distance[]; // 별의 거리

    private float star_y[]; //

    private int animation_count;

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

        // 부모 뷰(컨테이너)

        frameLayout_main = (FrameLayout)findViewById(R.id.frameLayout_main);

        // 터치 리스너
        frameLayout_main.setOnTouchListener(this);

        // 별 생성
        create_stars(NUM_STAR);

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

            star_list[i].setColorFilter(getResources().getColor(star_colors[random_star_color]));

            int star_dia = (int)(STAR_SIZE - Math.pow(random_z, 1.3));

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(star_dia, star_dia);
            star_list[i].setLayoutParams(lp);

            // 부모에 추가
            frameLayout_main.addView(star_list[i]);
        }
        // 별의 초기 위치를 초기화 하기 위해 실행한다.
        // 안하면 첫 이동시 위치가 순간적으로 변함.
        move_stars(0.0f, 0.0f);
    }




    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {

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

                    // 음수 양수를 통해 좌측 이동인지 우측 이동인지 판별
                    if(touch_move_distance > 0)
                    {
                        //move_stars(motionEvent.getX(), touch_pre_x - width * MAX_MAGNIFICATION, 10);
                        move_stars(width, current_x, 10); // 다음 페이지의 중앙값으로 가야 하므로 width 이 아니라  width + width / 2 로 설정해야 할듯.

                    }
                    else
                    {
                        //move_stars(motionEvent.getX(), touch_pre_x + width * MAX_MAGNIFICATION, 10);
                        move_stars(0, current_x, 10); // 다음 페이지의 중앙값으로 가야 하므로 0이 아니라 -width / 2 로 설정해야 할듯.

                    }


                }
                else
                {
                    // 반대로 일정 범위 이상 스와이프를 하지 못했을 경우
                    // 원래 자리로 돌아가야 한다.
                    // 터치를 종료한 지점 ~ 터치를 시작한 지점까지의 거리를 구해서
                    // 자연스럽게 원래 자리로 이동 시켜야 한다.

                    move_stars(touch_pre_x, current_x, 10);

                }

                break;
            // 터치중에 움직일 때.
            case MotionEvent.ACTION_MOVE:

                if(touch_move_pre_x != motionEvent.getX())
                {

                    move_stars(touch_move_pre_x, motionEvent.getX());
                    touch_move_pre_x = motionEvent.getX();

                }

                break;

            default:

                break;

        }

        return true;
    }

    private void move_stars(float pre_x, float post_x) {
        // 페이지당 움직여야할 총 거리를 정하고
        // 선형보간 방정식으로 움직여야할 것 같다.

        // 선형보간.

        for (int i = 0; i < star_list.length; i++) {

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
    }

    private void move_stars(float pre_x, float post_x, int interval)
    {

        animation_count = 0;

        int total_count = ANIMATION_TIME / interval;

        System.out.println("토탈 카운트 : " + total_count);

        float delta_distance = (pre_x - post_x) / total_count;

        System.out.println("거리 차이 : " + delta_distance);

        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {

            public void run(){

                runOnUiThread(new Runnable(){

                    public void run()
                    {

                        float move_pre_x = pre_x + (animation_count * delta_distance);

                        move_stars(move_pre_x, move_pre_x + delta_distance);
                        animation_count++;

                        if(animation_count == total_count)
                        {
                            timer.cancel();
                        }

                    }

                });

            }
        };

        timer.schedule(timerTask, 0, 10);

    }
}