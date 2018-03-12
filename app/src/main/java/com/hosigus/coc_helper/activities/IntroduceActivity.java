package com.hosigus.coc_helper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.LinearLayout;

import com.hosigus.coc_helper.R;

public class IntroduceActivity extends AppCompatActivity {


    public static void actionStart(Context context){
        Intent intent=new Intent(context,IntroduceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        Toolbar toolbar = findViewById(R.id.tb_toolbar);
        toolbar.setTitle("应用介绍");
        setSupportActionBar(toolbar);
        FloatingActionButton okFab = findViewById(R.id.fab_introduce_ok);
        okFab.setOnClickListener(v->{
            SharedPreferences preferences=getSharedPreferences("introduce",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor= preferences.edit();
            editor.putBoolean("showed", true);
            try {
                editor.putString("version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            editor.apply();
            MainActivity.actionStart(IntroduceActivity.this);
            finish();
        });
    }
}
