package com.hosigus.coc_helper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.InvestigatorRecycleAdapter;
import com.hosigus.coc_helper.utils.COCUtils;
import com.hosigus.coc_helper.views.dialogs.ShowInvestigatorDialog;

/**
 * Created by 某只机智 on 2018/2/17.
 */

public class InvestigatorPageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_page_investigator,container,false);
        RecyclerView rv = v.findViewById(R.id.rv_investigator);
        TextView emptyHint = v.findViewById(R.id.hint_empty);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        InvestigatorRecycleAdapter adapter=new InvestigatorRecycleAdapter(COCUtils.selectAllInvestigatorAsList(), i -> {
            // TODO: 2018/2/25
//            PopupMenu popupMenu=new PopupMenu(getActivity(),view);
//            popupMenu.getMenuInflater().inflate(R.menu.investigator,popupMenu.getMenu());
//            popupMenu.setOnMenuItemClickListener(item -> {
//                switch (item.getItemId()){
//                    case R.id.i_info:
//                        break;
//                    case R.id.i_att:
//                        break;
//                    case R.id.i_state:
//                        break;
//                    case R.id.i_skill:
//                        break;
//                }
//            });
//            popupMenu.show();
            new ShowInvestigatorDialog(getContext(), i);
        });
        if (adapter.getItemCount()==0)
            emptyHint.setVisibility(View.VISIBLE);
        rv.setAdapter(adapter);
        SwipeRefreshLayout srl = v.findViewById(R.id.srl_investigator);
        srl.setOnRefreshListener(() -> {
            adapter.refresh(COCUtils.selectAllInvestigatorAsList());
            if (adapter.getItemCount()==0)
                emptyHint.setVisibility(View.VISIBLE);
            srl.setRefreshing(false);
        });
        return v;
    }
}
