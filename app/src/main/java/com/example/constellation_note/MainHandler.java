package com.example.constellation_note;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

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
        //super.handleMessage(msg);

        if(msg.what == MainActivity.GET_LAST_CONSTELLATION_ID)
        {
            System.out.println("오옹 나이스 : " + msg.getData());
        }


    }
}
