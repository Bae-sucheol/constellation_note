package com.example.constellation_note;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class MainHandler extends Handler
{
    private static MainHandler mainHandler;

    private final WeakReference<MainActivity> weakReference;

    public MainHandler(MainActivity activity)
    {
        weakReference = new WeakReference<>(activity);
    }

    public static Handler getMainHandler(Context context)
    {
        if(mainHandler == null)
        {
            mainHandler = new MainHandler((MainActivity) context);
        }

        return mainHandler;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);

        MainActivity mainActivity = weakReference.get();

        switch(msg.what)
        {
            case MainActivity.GET_LAST_CONSTELLATION_ID :

                boolean isEmpty = msg.getData().getBoolean("isEmpty");
                int last_id = msg.getData().getInt("id");

                if(isEmpty)
                {

                    System.out.println("별자리가 없어용");

                }
                else
                {

                    System.out.println("반환받은 마지막 요소 : " + last_id);

                    mainActivity.select_ConstellationData(last_id);

                }

                break;

            case MainActivity.GET_CONSTELLATION_LIST :

                ArrayList<Constellation_data> returnValues = msg.getData().getParcelableArrayList("constellations");

                int size = returnValues.size();

                System.out.println("사이즈가 몇일까? : " + size);

                // 범위로 잡아야 하기 때문에 switch ~ case 말고 그냥 if로..

                // 1 ~ 3 까지는 그냥 화면에 뿌려주면 된다.
                if(size <= 3)
                {
                    for(int i = 0; i < size; i++)
                    {
                        mainActivity.create_constellation(returnValues.get(i));
                        System.out.println("받은값을 뿌려볼게 : " + returnValues.get(i));
                    }
                }
                else if(size == 4) // 3,4,1,2 순서
                {
                    mainActivity.create_constellation(returnValues.get(2));
                    mainActivity.create_constellation(returnValues.get(3));
                    mainActivity.create_constellation(returnValues.get(0));
                    mainActivity.create_constellation(returnValues.get(1));
                }
                else // n-2, n-1, n, 1, 2 순서
                {
                    mainActivity.create_constellation(returnValues.get(returnValues.size() - 3));
                    mainActivity.create_constellation(returnValues.get(returnValues.size() - 2));
                    mainActivity.create_constellation(returnValues.get(returnValues.size() - 1));
                    mainActivity.create_constellation(returnValues.get(0));
                    mainActivity.create_constellation(returnValues.get(1));
                }

                break;

            case MainActivity.GET_CONSTELLATION_SINGLE :

                ArrayList<Constellation_data> returnValue = msg.getData().getParcelableArrayList("constellations");

                mainActivity.swap_constellation_data(returnValue.get(0));

                System.out.println("스왑할 데이터 : " + returnValue.get(0).getId());

                break;

            case MainActivity.GET_STARS_LIST :

                ArrayList<Star_data> return_star_value = msg.getData().getParcelableArrayList("stars");

                Iterator<Star_data> iter = return_star_value.iterator();

                while(iter.hasNext())
                {
                    Star_data data = iter.next();

                    System.out.println(" 별 아이디 : " + data.get_id());
                    System.out.println(" 별자리 아이디 : " + data.getConstellation_id());
                }
                // 여기 까지는 잘 작동한다.

                mainActivity.set_stars_data(return_star_value);

                break;



            default:

                break;
        }

    }
}
