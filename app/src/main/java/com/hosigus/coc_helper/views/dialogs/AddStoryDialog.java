package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.utils.DensityUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

/**
 * Created by 某只机智 on 2018/2/17.
 */

public class AddStoryDialog extends Dialog {
    private CallBack callBack;
    private TextInputLayout titleTIL;;
    private TextInputLayout detailTIL;

    public AddStoryDialog(@NonNull Context context,CallBack back) {
        super(context);
        this.callBack=back;
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_story);

        titleTIL=findViewById(R.id.til_das_title);
        detailTIL = findViewById(R.id.til_das_detail);

        Button saveBtn = findViewById(R.id.btn_das_save);
        saveBtn.setOnClickListener((v) -> {
            String title=titleTIL.getEditText().getText().toString();
            String detail=detailTIL.getEditText().getText().toString();

            if (title.isEmpty()||detail.isEmpty()){
                ToastUtils.show("标题或内容为空!");
                return;
            }
            Story story=new Story();
            story.setTitle(title);
            story.setDetail(detail);
            callBack.save(story);
        });
    }

    public interface CallBack{
        void save(Story story);
    }
}
