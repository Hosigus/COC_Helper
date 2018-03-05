package com.hosigus.coc_helper;

import android.app.Application;
import android.content.Context;

/**
 * Created by 某只机智 on 2018/1/29.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
