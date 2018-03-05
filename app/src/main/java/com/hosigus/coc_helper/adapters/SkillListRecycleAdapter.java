package com.hosigus.coc_helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.items.Profession;
import com.hosigus.coc_helper.items.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/24.
 */

public class SkillListRecycleAdapter extends RecyclerView.Adapter<SkillListRecycleAdapter.SkillHolder> {

    private CallBack mCallBack;
    private List<Skill> skillList;
    private Profession profession;

    public SkillListRecycleAdapter(Profession pro,List<Skill> mList,CallBack callBack) {
        mCallBack = callBack;
        skillList = mList;
        profession = pro;
    }

    @Override
    public SkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SkillHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill_2,parent,false));
    }

    @Override
    public void onBindViewHolder(SkillHolder holder, int position) {
        holder.setSkill(skillList.get(position));
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    class SkillHolder extends RecyclerView.ViewHolder {
        private TextView name,start,isPro;
        private Skill mSkill;
        SkillHolder(View v) {
            super(v);
            v.setOnClickListener(vie->{
                int position = getAdapterPosition();
                if (skillList.get(position).getId()!=1){
                    skillList.remove(position);
                    notifyItemRemoved(position);
                }
                mCallBack.onSelect(mSkill);
            });
            name = v.findViewById(R.id.tv_item_skill_name);
            start = v.findViewById(R.id.tv_item_skill_start);
            isPro = v.findViewById(R.id.tv_item_skill_ispro);
        }
        void setSkill(Skill skill){
            mSkill = skill;
            String text=skill.getExtra();
            text=text==null?"":"("+text+")";
            text=skill.getName()+text;
            this.name.setText(text);
            start.setText(String.valueOf(skill.getStartPoint()));
            isPro.setText(profession.isProSkill(skill)?"√":"×");
        }
    }

    public interface CallBack{
        void onSelect(Skill skill);
    }
}
