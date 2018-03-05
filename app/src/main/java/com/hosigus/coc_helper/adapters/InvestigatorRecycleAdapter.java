package com.hosigus.coc_helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.utils.FileUtils;
import com.hosigus.coc_helper.views.HintEditView;

import java.util.List;

/**
 * Created by 某只机智 on 2018/2/25.
 */

public class InvestigatorRecycleAdapter extends RecyclerView.Adapter<InvestigatorRecycleAdapter.IHolder> {
    private List<Investigator> iList;
    private CallBack mCallBack;

    public InvestigatorRecycleAdapter(List<Investigator> iList,CallBack callBack) {
        this.iList = iList;
        this.mCallBack=callBack;
    }
    @Override
    public IHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_investigator, parent, false));
    }

    @Override
    public void onBindViewHolder(IHolder holder, int position) {
        holder.setInvestigator(iList.get(position));
        holder.getV().setOnClickListener(v -> {
            mCallBack.onSelect(holder.getI());
        });
    }

    @Override
    public int getItemCount() {
        return iList.size();
    }

    public void refresh(List<Investigator> newList) {
        iList=newList;
        notifyDataSetChanged();
    }

    class IHolder extends RecyclerView.ViewHolder {
        private View v;
        private Investigator mI;
        private HintEditView name,age,sex,pro;
        private ImageView head;
        IHolder(View v) {
            super(v);
            this.v = v;
            head = v.findViewById(R.id.iv_item_i_head);
            name = v.findViewById(R.id.hev_item_i_name);
            age = v.findViewById(R.id.hev_item_i_age);
            sex = v.findViewById(R.id.hev_item_i_sex);
            pro = v.findViewById(R.id.hev_item_i_pro);
        }
        void setInvestigator(Investigator i){
            mI=i;
            name.setEditText(i.getName());
            age.setEditText(String.valueOf(i.getAge()));
            sex.setEditText(i.getSex());
            pro.setEditText(i.getProfession().getName());
            FileUtils.readBitmapInto(head,i.getName()+i.getId());
        }

        public View getV() {
            return v;
        }

        public Investigator getI() {
            return mI;
        }
    }

    public interface CallBack{
        void onSelect(Investigator i);
    }
}
