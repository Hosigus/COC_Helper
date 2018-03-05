package com.hosigus.coc_helper.utils;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.hosigus.coc_helper.MyApplication;

/**
 * Created by 某只机智 on 2018/2/16.
 */

public class ToastUtils {
    private static Toast toast;
    public static void show(String text){
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }
}
