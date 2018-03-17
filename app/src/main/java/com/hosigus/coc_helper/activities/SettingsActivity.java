package com.hosigus.coc_helper.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.configs.Settings;
import com.hosigus.coc_helper.utils.ToastUtils;

public class SettingsActivity extends AppCompatPreferenceActivity {
    public static void actionStart(Context context){
        Intent intent=new Intent(context,SettingsActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        addPreferencesFromResource(R.xml.pref_settings);

        findPreference("about").setOnPreferenceClickListener(v->{
            IntroduceActivity.actionStart(SettingsActivity.this);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        ToastUtils.show("新改动需要重启才生效哦");
        super.onDestroy();
    }
}
