package com.hosigus.coc_helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.items.Profession;
import com.hosigus.coc_helper.items.Skill;

import java.util.List;

/**
 * Created by 某只机智 on 2018/2/23.
 */

public class SkillRecycleAdapter extends RecyclerView.Adapter<SkillRecycleAdapter.SkillHolder> {
    private Profession profession;
    private List<Skill> skillList;

    private ChangeCallBack mChangeCallBack;
    private SelectCallBack mSelectCallBack;

    public SkillRecycleAdapter(List<Skill> skillList,Profession profession,ChangeCallBack changeCallBack) {
        this.skillList = skillList;
        this.profession = profession;
        mChangeCallBack = changeCallBack;
    }

    public SkillRecycleAdapter(List<Skill> skillList) {
        this.skillList = skillList;
    }
    public SkillRecycleAdapter(List<Skill> skillList,SelectCallBack callBack) {
        this.skillList = skillList;
        this.mSelectCallBack = callBack;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void addSkill(Skill skill){
        skillList.add(skill);
        notifyItemInserted(skillList.size());
    }

    @Override
    public SkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SkillHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill_1,parent,false));
    }

    @Override
    public void onBindViewHolder(SkillHolder holder, int position) {
        if (profession==null)
            holder.setStaticSkill(skillList.get(position));
        else
            holder.setSkill(skillList.get(position));
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    class SkillHolder extends RecyclerView.ViewHolder {
        private View v;
        private TextView nameTV,startTV,sumTV;
        private EditText proET,intET;
        private String lastPro,lastInt;
        SkillHolder(View v) {
            super(v);
            this.v = v;
            nameTV = v.findViewById(R.id.tv_item_skill_name);
            startTV = v.findViewById(R.id.tv_item_skill_start);
            sumTV = v.findViewById(R.id.tv_item_skill_sum);
            proET = v.findViewById(R.id.et_item_skill_pro);
            intET = v.findViewById(R.id.et_item_skill_int);
        }
        private void tryChangePro(){
            int newP=Integer.parseInt(proET.getText().toString());
            if(getSum()>100||!mChangeCallBack.tryChangePro(newP-Integer.parseInt(lastPro))){
                proET.setText(lastPro);
            }else {
                sumTV.setText(String.valueOf(getSum()));
                skillList.get(getAdapterPosition()).setProPoint(newP);
            }
        }
        private void tryChangeInt(){
            int newP=Integer.parseInt(intET.getText().toString());
            if(getSum()>100||!mChangeCallBack.tryChangeInt(newP-Integer.parseInt(lastInt))){
                intET.setText(lastInt);
            }else {
                sumTV.setText(String.valueOf(getSum()));
                skillList.get(getAdapterPosition()).setIntPoint(newP);
            }
        }
        void setSkill(Skill skill){
            nameTV.setText(skill.getName());
            startTV.setText(String.valueOf(skill.getStartPoint()));
            if (skill.getId()==25||profession.isProSkill(skill,skillList)) {
                proET.setText(String.valueOf(skill.getProPoint()));
                proET.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus){
                        lastPro =proET.getText().toString();
                    }else if (proET.getText().toString().isEmpty()){
                        proET.setText(lastPro);
                    }else {
                        tryChangePro();
                    }
                });
            } else {
                proET.setEnabled(false);
            }
            if (skill.getId()==25) {
                intET.setEnabled(false);
            }else{
                intET.setText(String.valueOf(skill.getIntPoint()));
                intET.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus){
                        lastInt =intET.getText().toString();
                    }else if (intET.getText().toString().isEmpty()) {
                        intET.setText(lastInt);
                    }else {
                        tryChangeInt();
                    }
                });
            }
            sumTV.setText(String.valueOf(skill.getSumPoint()));
        }

        private int getSum() {
            int sum=0;
            String s,i,p;
            s = startTV.getText().toString();
            i = intET.getText().toString();
            p = proET.getText().toString();
            if (!s.isEmpty())
                sum += Integer.parseInt(s);
            if (!i.isEmpty())
                sum += Integer.parseInt(i);
            if (!p.isEmpty())
                sum += Integer.parseInt(p);
            return sum;
        }

        void setStaticSkill(Skill skill){
            if (mSelectCallBack!=null)
                v.setOnClickListener(v1 ->mSelectCallBack.select(skill));
            nameTV.setText(skill.getName());
            startTV.setText(String.valueOf(skill.getStartPoint()));
            proET.setEnabled(false);
            intET.setEnabled(false);
            intET.setText(String.valueOf(skill.getIntPoint()));
            proET.setText(String.valueOf(skill.getProPoint()));
            sumTV.setText(String.valueOf(skill.getSumPoint()));
        }
    }

    public interface ChangeCallBack {
        boolean tryChangePro(int point);
        boolean tryChangeInt(int point);
    }

    public interface SelectCallBack{
        void select(Skill skill);
    }
}
