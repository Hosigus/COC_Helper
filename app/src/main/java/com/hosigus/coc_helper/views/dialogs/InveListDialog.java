package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.InvestigatorRecycleAdapter;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.utils.COCUtils;
import com.hosigus.coc_helper.utils.DensityUtils;

/**
 * Created by 某只机智 on 2018/2/25.
 */

public class InveListDialog extends Dialog {
    private InvestigatorRecycleAdapter.CallBack callBack;
    public InveListDialog(@NonNull Context context,InvestigatorRecycleAdapter.CallBack callBack) {
        super(context);
        DensityUtils.setScreenFullWidth(context,getWindow(),0);
        this.callBack=callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_list_inve);

        RecyclerView rv = findViewById(R.id.rv_lid);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new InvestigatorRecycleAdapter(COCUtils.selectAllInvestigatorAsList(), i -> {
            dismiss();
            callBack.onSelect(i);
        }));
    }
}
