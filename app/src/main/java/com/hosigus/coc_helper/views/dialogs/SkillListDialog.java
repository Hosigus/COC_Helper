package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.SkillListRecycleAdapter;
import com.hosigus.coc_helper.adapters.SkillRecycleAdapter;
import com.hosigus.coc_helper.utils.DensityUtils;

/**
 * Created by 某只机智 on 2018/2/24.
 */

public class SkillListDialog extends Dialog{
    private SkillListRecycleAdapter slrAdapter;
    private SkillRecycleAdapter srAdapter;

    public SkillListDialog(@NonNull Context context,SkillListRecycleAdapter adapter) {
        super(context);
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
        this.slrAdapter = adapter;
    }
    public SkillListDialog(@NonNull Context context,SkillRecycleAdapter adapter) {
        super(context);
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
        this.srAdapter = adapter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (slrAdapter!=null){
            setContentView(R.layout.dialog_list_skill);
            RecyclerView rv = findViewById(R.id.rv_lsd);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(slrAdapter);
        }else if (srAdapter!=null){
            setContentView(R.layout.dialog_show_i_skill);
            RecyclerView rv = findViewById(R.id.rv_dsi);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(srAdapter);
        }
    }
}
