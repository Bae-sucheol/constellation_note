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

        switch(msg.what)
        {
            case MainActivity.GET_LAST_CONSTELLATION_ID :

                int last_id = msg.getData().getInt("id");

                System.out.println("반환받은 값 : " + last_id);

                if(last_id == 0)
                {
                    // 아무것도 안해도 됨.
                    // 나중에 추가할게 있으면 추가하자.
                }
                else
                {

                    System.out.println("반환받은 마지막 요소 : " + last_id);

                    MainActivity activity = weakReference.get();

                    activity.select_ConstellationData(last_id);

                }

                break;

            case MainActivity.GET_CONSTELLATION_LIST :

                System.out.println("값을 받았어." + msg.getData());

                ArrayList<Constellation_data> returnValues = msg.getData().getParcelableArrayList("constellations");

                Iterator<Constellation_data> iterator = returnValues.iterator();

                while(iterator.hasNext())
                {
                    Constellation_data constellation_data = iterator.next();
                    System.out.println("아이디 : " + constellation_data.getId());
                    System.out.println("타이틀 : " + constellation_data.getTitle());
                }

                break;

            default:

                break;
        }

    }
}
