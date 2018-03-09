package com.hosigus.coc_helper.fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    private InvestigatorRecycleAdapter adapter;
    private SwipeRefreshLayout emptyHint;
    private SwipeRefreshLayout srl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_page_investigator,container,false);
        RecyclerView rv = v.findViewById(R.id.rv_investigator);
        emptyHint = v.findViewById(R.id.hint_empty);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new InvestigatorRecycleAdapter(COCUtils.selectAllInvestigatorAsList(), i -> {
            new ShowInvestigatorDialog(getContext(), i);
        });
        if (adapter.getItemCount()==0){
            emptyHint.setVisibility(View.VISIBLE);
            emptyHint.setOnRefreshListener(this::refreshI);
        }
        rv.setAdapter(adapter);

        srl = v.findViewById(R.id.srl_investigator);
        srl.setOnRefreshListener(this::refreshI);
        ItemTouchHelper touchHelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                adapter.swapData(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(getContext())
                        .setTitle("删除人物卡")
                        .setMessage("删除后无法恢复,确定删除吗?")
                        .setPositiveButton("确认",(d,w)->adapter.delData(viewHolder.getAdapterPosition()))
                        .setNegativeButton("取消",(d,w)->{})
                        .create().show();
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    float alpha = 1 - Math.abs(1.0f * dX / viewHolder.itemView.getWidth());
                    viewHolder.itemView.setAlpha(alpha);
                }
            }
        });
        touchHelper.attachToRecyclerView(rv);
        return v;
    }

    private void refreshI() {
        adapter.refresh(COCUtils.selectAllInvestigatorAsList());
        if (adapter.getItemCount()==0)
            emptyHint.setVisibility(View.VISIBLE);
        else
            emptyHint.setVisibility(View.GONE);

        if (emptyHint.isRefreshing())
            emptyHint.setRefreshing(false);
        if (srl.isRefreshing())
            srl.setRefreshing(false);
    }
}
