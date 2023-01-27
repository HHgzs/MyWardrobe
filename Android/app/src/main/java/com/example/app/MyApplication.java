package com.example.app;

import android.app.Application;
import android.content.Context;

import com.example.app.entity.noteInfo;

import java.util.List;

public class MyApplication extends Application {

    private static Context context;

    public static List<noteInfo> noteInfoList;



    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
