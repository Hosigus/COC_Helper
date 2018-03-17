package com.hosigus.coc_helper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.configs.NetConfig;
import com.hosigus.coc_helper.configs.Settings;
import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.utils.COCSQLHelper;
import com.hosigus.coc_helper.utils.NetConnectUtils;

public class WelcomeActivity extends AppCompatActivity {
    public static SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initData();
    }

    void initData(){
        db = new COCSQLHelper().getWritableDatabase();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences=getSharedPreferences("introduce",Context.MODE_PRIVATE);
        try {
            if (preferences.getBoolean("showed",false)&&preferences.getString("version","").equals(getPackageManager().getPackageInfo(getPackageName(),0).versionName))
                MainActivity.actionStart(WelcomeActivity.this);
            else
                IntroduceActivity.actionStart(WelcomeActivity.this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Settings.initData();
        finish();
    }
}
