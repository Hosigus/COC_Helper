package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.SkillListRecycleAdapter;
import com.hosigus.coc_helper.adapters.SkillRecycleAdapter;
import com.hosigus.coc_helper.items.Attributes;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.items.Profession;
import com.hosigus.coc_helper.items.Skill;
import com.hosigus.coc_helper.utils.COCUtils;
import com.hosigus.coc_helper.utils.DensityUtils;
import com.hosigus.coc_helper.utils.FileUtils;
import com.hosigus.coc_helper.utils.ToastUtils;
import com.hosigus.coc_helper.views.HintEditView;
import com.hosigus.coc_helper.views.PolygonView;

import java.util.ArrayList;
import java.util.List;

import static com.hosigus.coc_helper.activities.WelcomeActivity.db;

/**
 * Created by 某只机智 on 2018/2/15.
 *
 */

public class AddInvestigatorDialog extends Dialog implements AdapterView.OnItemSelectedListener {
    private View view1,view2,view3;
    private TextView hintProfessionTV;
    private Spinner sexS;
    private ImageView headIV;
    private List<HintEditView> v1HevList;
    private List<HintEditView> attHevList;
    private List<HintEditView> v3HevList;
    private PolygonView polygonView;
    private SkillListDialog slDialog;
    private SkillRecycleAdapter sradapter;

    private CallBack mCallBack;

    private List<Profession> professionList;
    private List<String> prfessionNameList;

    private Profession mProfession;
    private Attributes attributes;
    private int proPoint;
    private int intPoint;
    private boolean canSave;

    public AddInvestigatorDialog(@NonNull Context context,CallBack callBack) {
        super(context);
        this.mCallBack=callBack;

        DensityUtils.setScreenFullWidth(context,getWindow(),32);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        setContentView(view1);
        canSave=false;
    }

    private void initViews(){
        LayoutInflater mInflater=getLayoutInflater();
        view1 = mInflater.inflate(R.layout.dialog_add_investigator_1,null,false);
        view2 = mInflater.inflate(R.layout.dialog_add_investigator_2, null, false);
        view3 = mInflater.inflate(R.layout.dialog_add_investigator_3, null, false);

        headIV = view1.findViewById(R.id.iv_dai_headpic);
        hintProfessionTV = view1.findViewById(R.id.tv_dai_profession_detail);
        polygonView = view2.findViewById(R.id.pv_dai);
        Spinner proS = view1.findViewById(R.id.s_dai_profession);
        sexS = view1.findViewById(R.id.s_dai_sex);
        Button confirmBtn = view1.findViewById(R.id.btn_dai_confirm);
        Button backBtn = view2.findViewById(R.id.btn_dai_back);
        Button confirm2Btn = view2.findViewById(R.id.btn_dai_confirm2);
        Button diceBtn = view2.findViewById(R.id.btn_dai_dice);
        Button refreshBtn = view2.findViewById(R.id.btn_dai_refresh);
        FloatingActionButton addSBtn = view3.findViewById(R.id.fab_dai_add);
        initHevList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, prfessionNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        proS.setAdapter(adapter);
        proS.setOnItemSelectedListener(this);
        headIV.setOnClickListener(v-> mCallBack.choosePic());
        confirmBtn.setOnClickListener(v-> {
            for (HintEditView hev:v1HevList) {
                if (hev.getEditText().equals("")) {
                    ToastUtils.show("重要信息未填写");
                    return;
                }
            }
            if (v1HevList.get(0).getEditText().length()>10||v1HevList.get(2).getEditText().length()>10||
                    v1HevList.get(3).getEditText().length()>20||v1HevList.get(3).getEditText().length()>20){
                ToastUtils.show("太..太长了..里面..会坏掉的..");
                return;
            }
            setContentView(view2);
        });
        confirm2Btn.setOnClickListener(v-> setView3());
        backBtn.setOnClickListener(v->setContentView(view1));
        diceBtn.setOnClickListener(v-> randomPolygon());
        refreshBtn.setOnClickListener(v->{
            List<Float> pointValue = new ArrayList<>();
            for (HintEditView hev: attHevList){
                String text=hev.getEditText();
                if (text.isEmpty()){
                    hev.setEditText("0");
                    text = "0";
                }
                pointValue.add(Integer.parseInt(text) /100f);
            }
            polygonView.setPointValue(pointValue);
            polygonView.draw();
        });
        addSBtn.setOnClickListener(v-> slDialog.show());

        randomPolygon();
    }

    private void setView3() {
        initArr();

        List<Integer> skillIdList = new ArrayList<>();
        skillIdList.add(96);
        skillIdList.add(28);
        skillIdList.add(25);
        slDialog=new SkillListDialog(getContext(),new SkillListRecycleAdapter(mProfession, COCUtils.selectSkillListWithoutList(skillIdList), skill -> {
            sradapter.addSkill(skill);
            slDialog.dismiss();
        }));

        proPoint = COCUtils.parserSkillPoint(attributes,mProfession.getAttribute());
        intPoint = attributes.getInt()*2;
        List<Skill> skillList = new ArrayList<>();
        skillList.add(new Skill(96,"母语",null,attributes.getEdu()));
        skillList.add(new Skill(28,"闪避",null,attributes.getDex()/2));
        Skill creditSkill=new Skill(25,"信誉",null,0);
        String credit=mProfession.getCredit();
        credit=credit.substring(0,credit.indexOf('-'));
        creditSkill.setProPoint(Integer.parseInt(credit));
        skillList.add(creditSkill);
        sradapter =new SkillRecycleAdapter(skillList, mProfession, new SkillRecycleAdapter.ChangeCallBack() {
            @Override
            public boolean tryChangePro(int point) {
                if (point > proPoint)
                    return false;
                proPoint -= point;
                v3HevList.get(3).setEditText(String.valueOf(proPoint));
                return true;
            }

            @Override
            public boolean tryChangeInt(int point) {
                if (point > intPoint)
                    return false;
                intPoint -= point;
                v3HevList.get(4).setEditText(String.valueOf(intPoint));
                return true;
            }
        });
        
        RecyclerView rv = view3.findViewById(R.id.rv_dai_skill);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(sradapter);

        proPoint -= Integer.parseInt(credit);

        v3HevList.get(0).setEditText(v1HevList.get(0).getEditText());
        v3HevList.get(1).setEditText(mProfession.getName());
        v3HevList.get(2).setEditText(mProfession.getCredit());
        v3HevList.get(3).setEditText(String.valueOf(proPoint));
        v3HevList.get(4).setEditText(String.valueOf(intPoint));
        
        setContentView(view3);
        canSave = true;
    }

    private void initArr() {
        for (HintEditView hev: attHevList)
            if (hev.getEditText().isEmpty())
                hev.setEditText("0");

        List<Integer> list = new ArrayList<>();
        for (HintEditView hev: attHevList)
            list.add(Integer.parseInt(hev.getEditText()));

        attributes = new Attributes(list);
    }

    private void initHevList() {
        v1HevList = new ArrayList<>();
        attHevList = new ArrayList<>();
        v3HevList = new ArrayList<>();
        v1HevList.add(view1.findViewById(R.id.hev_dai_name));
        v1HevList.add(view1.findViewById(R.id.hev_dai_age));
        v1HevList.add(view1.findViewById(R.id.hev_dai_decade));
        v1HevList.add(view1.findViewById(R.id.hev_dai_address));
        v1HevList.add(view1.findViewById(R.id.hev_dai_homeland));
        attHevList.add(view2.findViewById(R.id.hev_dai_str));
        attHevList.add(view2.findViewById(R.id.hev_dai_dex));
        attHevList.add(view2.findViewById(R.id.hev_dai_pow));
        attHevList.add(view2.findViewById(R.id.hev_dai_con));
        attHevList.add(view2.findViewById(R.id.hev_dai_app));
        attHevList.add(view2.findViewById(R.id.hev_dai_edu));
        attHevList.add(view2.findViewById(R.id.hev_dai_siz));
        attHevList.add(view2.findViewById(R.id.hev_dai_int));
        attHevList.add(view2.findViewById(R.id.hev_dai_luck));
        v3HevList.add(view3.findViewById(R.id.hev_dai_name));
        v3HevList.add(view3.findViewById(R.id.hev_dai_profession));
        v3HevList.add(view3.findViewById(R.id.hev_dai_credit));
        v3HevList.add(view3.findViewById(R.id.hev_dai_pro_point));
        v3HevList.add(view3.findViewById(R.id.hev_dai_int_point));

        for (int i = 0; i < 9; i++) {
            HintEditView hev = attHevList.get(i);
            final int num = i;
            hev.setOnChangeListener((v, actionId, event) ->{
                String text=hev.getEditText();
                if (text.isEmpty()){
                    hev.setEditText("0");
                    text = "0";
                }
                polygonView.setPointValue(num, Integer.parseInt(text)/100f);
                polygonView.draw();
                return false;
            });
        }
    }

    private void randomPolygon() {
        List<Float> pointValue = new ArrayList<>();
        List<String> pointStr = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int roll=(int)(Math.random()*6);
            roll+=(int)(Math.random()*6);
            roll+=(int)(Math.random()*6);
            roll=roll*5+15;

            pointValue.add(roll/100f);
            pointStr.add(String.valueOf(roll));
        }
        polygonView.setPointValue(pointValue);
        polygonView.draw();
        setEditViewText(pointStr);
    }

    private void initData(){
        FileUtils.deleteFile("temp");
        Cursor cursor=db.rawQuery("SELECT * FROM profession",new String[]{});
        professionList=new ArrayList<>();
        prfessionNameList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                String name=cursor.getString(cursor.getColumnIndex("name"));
                prfessionNameList.add(name);

                Profession profession=new Profession(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        name,
                        cursor.getString(cursor.getColumnIndex("credit")),
                        cursor.getString(cursor.getColumnIndex("attribute")));
                professionList.add(profession);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setEditViewText(List<String> textList){
        for (int i = 0; i < 9; i++) {
            attHevList.get(i).setEditText(textList.get(i));
        }
    }

    public void setHeadImage(){
        headIV.setImageBitmap(FileUtils.getBitmapFromFile("temp"));
    }

    private void saveInvestigator(){
        String name = v1HevList.get(0).getEditText();
        Investigator i=new Investigator();
        i.setName(name);
        i.setSex(getSex());
        i.setAge(Integer.valueOf(v1HevList.get(1).getEditText()));
        i.setDecade(v1HevList.get(2).getEditText());
        i.setAddress(v1HevList.get(3).getEditText());
        i.setHomeland(v1HevList.get(4).getEditText());
        i.setProfession(mProfession);
        i.setAttributes(attributes);
        i.setLearnedSkillList(sradapter.getSkillList());
        ProgressDialog dialog = ProgressDialog.show(getContext(), "尝试造人中……", "给"+name+"接生中");
        COCUtils.saveInvestigator(i,state -> {
            dialog.dismiss();
            if (state)
                ToastUtils.show(name+"已诞生");
            else
                ToastUtils.show(name+"已被献祭");
        });
    }

    private String getSex() {
        switch (sexS.getSelectedItemPosition()){
            case 1:return "男";
            case 2:return "女";
        }
        return "人妖";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mProfession=professionList.get(position);
        String text = "职业:" + mProfession.getName() +
                "\u3000信誉范围:" + mProfession.getCredit() +
                "\u3000职业属性:" + mProfession.getAttribute() +
                "\n本职技能:" + mProfession.getSkillsString();
        hintProfessionTV.setText(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void dismiss() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("确定要关闭吗？");
        builder.setMessage("只有设置过技能后关闭,人物卡才能被保存");
        builder.setPositiveButton("保存", (dialog, which) -> {
            if (canSave){
                saveInvestigator();
                super.dismiss();
            }else {
                ToastUtils.show("不满足保存条件");
            }
        });
        builder.setNegativeButton("不保存",(dialog, which) -> {super.dismiss();});
        builder.setNeutralButton("取消", (dialog, which) -> {});
        builder.create().show();
    }

    public interface CallBack{
        void choosePic();
    }

}
