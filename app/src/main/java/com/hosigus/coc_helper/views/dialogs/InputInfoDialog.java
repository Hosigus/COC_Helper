package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.views.HintEditView;

import java.util.List;

/**
 * Created by 某只机智 on 2018/2/27.
 */

public class InputInfoDialog extends Dialog {

    public static final int TYPE_ROOM = 1;
    public static final int TYPE_SPACE = 2;
    public static final int TYPE_DICE = 3;

    private int type;
    private ConfirmListener confirm;
    private BackListener back;

    public InputInfoDialog(@NonNull Context context,ConfirmListener confirm,int type){
        super(context);
        this.confirm = confirm;
        this.type = type;
    }

    public void setBackListener(BackListener back) {
        this.back = back;
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_info);

        HintEditView info1, info2;
        info1 = findViewById(R.id.hev_dii_info1);
        info2 = findViewById(R.id.hev_dii_info2);

        Button confirmBtn = findViewById(R.id.btn_dii_confirm);
        confirmBtn.setOnClickListener(v -> confirm.onConfirm(info1.getEditText(), info2.getEditText()));

        //强行复用
        if (type==TYPE_SPACE){
            TextView textView = findViewById(R.id.tv_dii_title);
            textView.setText("骰子空间信息");
            info1.setHintText("主题");
            info1.setEditText("一次关于骰子的骰子");
            info2.setVisibility(View.GONE);
        }else if (type==TYPE_DICE){
            TextView textView = findViewById(R.id.tv_dii_title);
            textView.setText("按格式掷骰 eg: 2d3+4");
            info1.setHintText("注释：");
            info2.setHintText("掷骰公式：");
        }
    }

    @Override
    public void onBackPressed() {
        if (back==null)
            super.onBackPressed();
        else if (back.onBack()){
            super.onBackPressed();
            dismiss();
        }
    }

    public interface BackListener{
        boolean onBack();
    }
    public interface ConfirmListener{
        void onConfirm(String info1,String info2);
    }
}
