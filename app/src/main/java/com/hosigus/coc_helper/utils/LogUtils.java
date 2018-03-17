package com.hosigus.coc_helper.utils;

import android.util.Log;

/**
 * Created by 某只机智 on 2018/3/14.
 */

public class LogUtils {
    private static final int ALL = 0;
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    public static final int NOTHING = 6;
    private static int flag = ALL;

    public static void v(String tag, String msg) {
        if (flag <= VERBOSE) {
            Log.v(tag, msg);
        }
    }
    public static void d(String tag, String msg) {
        if (flag <= DEBUG) {
            Log.d(tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if (flag <= INFO) {
            Log.i(tag, msg);
        }
    }
    public static void w(String tag, String msg) {
        if (flag <= WARN) {
            Log.w(tag, msg);
        }
    }
    public static void e(String tag, String msg) {
        if (flag <= ERROR) {
            Log.e(tag, msg);
        }
    }
}
