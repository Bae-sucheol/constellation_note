package com.example.constellation_note;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class MainThreadHandler extends Handler
{

    public MainThreadHandler(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg)
    {
        super.handleMessage(msg);

        System.out.println(msg.getData());
    }
}
