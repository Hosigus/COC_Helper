package com.hosigus.coc_helper.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.SkillRecycleAdapter;
import com.hosigus.coc_helper.items.Attributes;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.utils.DensityUtils;
import com.hosigus.coc_helper.utils.FileUtils;
import com.hosigus.coc_helper.views.HintEditView;
import com.hosigus.coc_helper.views.PolygonView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/28.
 */

public class ShowInvestigatorDialog extends Dialog{
    public static final int ATT=0,STATE=1,SKILL=2,INFO=3;
    private Investigator i;
    private int type;
    public ShowInvestigatorDialog(@NonNull Context context, Investigator i) {
        super(context);
        this.i=i;
        chooseDialogType();
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
    }
    private void chooseDialogType() {
        final String items[] = {"属性值", "状态", "持有技能","基本信息"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),3);
        builder.setTitle("选择查看项目");
        builder.setItems(items, (dialog, which) -> {
            type = which;
            dialog.dismiss();
            show();
        });
        builder.create().show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (type==ATT)
            initAtt();
        else if (type==STATE)
            initState();
        else if (type==SKILL)
            initSkill();
        else if (type==INFO)
            initInfo();

    }

    private void initSkill() {
        setContentView(R.layout.dialog_show_i_skill);
        RecyclerView rv = findViewById(R.id.rv_dsi);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new SkillRecycleAdapter(i.getLearnedSkillList()));
    }

    private void initState() {
        setContentView(R.layout.dialog_show_i_state);
        Attributes att = i.getAttributes();
        HintEditView lifeV = findViewById(R.id.hev_dsi_life);
        HintEditView sanV = findViewById(R.id.hev_dsi_san);
        HintEditView mpV = findViewById(R.id.hev_dsi_mp);
        HintEditView stateV = findViewById(R.id.hev_dsi_state);

        lifeV.setEditText(att.getLife()+" / "+att.getLifeMax());
        sanV.setEditText(att.getSan()+" / "+att.getSanMax());
        mpV.setEditText(att.getMagic()+" / "+att.getMagicMax());
        stateV.setEditText(att.getState());
    }

    private void initInfo() {
        setContentView(R.layout.dialog_add_investigator_1);
        List<HintEditView> hevList = new ArrayList<>();
        hevList.add((HintEditView)findViewById(R.id.hev_dai_name));
        hevList.add((HintEditView)findViewById(R.id.hev_dai_age));
        hevList.add((HintEditView)findViewById(R.id.hev_dai_decade));
        hevList.add((HintEditView)findViewById(R.id.hev_dai_address));
        hevList.add((HintEditView)findViewById(R.id.hev_dai_homeland));

        hevList.get(0).setEditText(i.getName());
        hevList.get(1).setEditText(String.valueOf(i.getAge()));
        hevList.get(2).setEditText(i.getDecade());
        hevList.get(3).setEditText(i.getAddress());
        hevList.get(4).setEditText(i.getHomeland());

        for (HintEditView hev : hevList) {
            hev.setEditAble(false);
        }

        HintEditView sexHev = findViewById(R.id.hev_dai_sex);
        sexHev.setEditText(i.getSex());
        sexHev.setVisibility(View.VISIBLE);
        HintEditView proHev = findViewById(R.id.hev_dai_pro);
        proHev.setEditText(i.getProfession().getName());
        proHev.setVisibility(View.VISIBLE);
        ImageView headIV = findViewById(R.id.iv_dai_headpic);
        FileUtils.readBitmapInto(headIV,i.getName()+i.getId());

        LinearLayout sexLL = findViewById(R.id.ll_dai_sex);
        sexLL.setVisibility(View.GONE);
        LinearLayout proLL = findViewById(R.id.ll_dai_pro);
        proLL.setVisibility(View.GONE);
        Button confirmBtn = findViewById(R.id.btn_dai_confirm);
        confirmBtn.setVisibility(View.GONE);
    }

    private void initAtt() {
        setContentView(R.layout.dialog_add_investigator_2);
        List<Button> buttonList = new ArrayList<>();
        buttonList.add((Button) findViewById(R.id.btn_dai_dice));
        buttonList.add((Button) findViewById(R.id.btn_dai_refresh));
        buttonList.add((Button) findViewById(R.id.btn_dai_confirm2));
        buttonList.add((Button) findViewById(R.id.btn_dai_back));
        for (Button button:buttonList) {
            button.setVisibility(View.GONE);
        }
        List<HintEditView> attHevList = new ArrayList<>();
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_str));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_dex));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_pow));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_con));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_app));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_edu));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_siz));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_int));
        attHevList.add((HintEditView)findViewById(R.id.hev_dai_luck));

        List<Integer> attList = i.getAttributes().getAttAsList();

        for (int j = 0; j < 9; j++) {
            attHevList.get(j).setEditText(String.valueOf(attList.get(j)));
            attHevList.get(j).setEditAble(false);
        }

        PolygonView polygonView = findViewById(R.id.pv_dai);
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

    }
}
