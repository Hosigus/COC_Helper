package com.hosigus.coc_helper.configs;

import android.preference.PreferenceManager;

import com.hosigus.coc_helper.MyApplication;

/**
 * Created by 某只机智 on 2018/2/17.
 */

public class Settings {
    public static int storyNumInPage;
    public static boolean needAnimator;

    public static void initData(){
        String numStr = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext())
                .getString("storyNumInPage", "15");
        storyNumInPage = numStr.isEmpty()?15:Integer.parseInt(numStr);
        needAnimator=PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext())
                .getBoolean("needAnimator", true);
    }
}
