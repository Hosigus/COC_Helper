package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.activities.GameActivity;
import com.hosigus.coc_helper.adapters.SkillRecycleAdapter;
import com.hosigus.coc_helper.configs.NetConfig;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.items.Record;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.utils.COCUtils;
import com.hosigus.coc_helper.utils.DensityUtils;
import com.hosigus.coc_helper.utils.NetConnectUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

/**
 * Created by 某只机智 on 2018/2/28.
 */

public class ShowRecordDialog extends Dialog {
    private Record record;
    private int type;
    private TextView detailV;
    private StringBuilder sb;
    private KPBtnListener mKPBtnListener;
    private PCBtnListener mPCBtnListener;
    private Investigator investigator;
    private InputInfoDialog iid;
    private SkillListDialog sld;

    public ShowRecordDialog(@NonNull Context context, int mType) {
        super(context);
        this.type = mType;
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
        DensityUtils.setScreenFullHeight(context,getWindow(),32);
        setCancelable(false);
    }

    public void addDetail(String newD){
        sb.append("\n").append(newD);
        detailV.setText(sb.toString());
        record.setDetail(sb.toString());
    }

    public void setKPBtnListener(KPBtnListener mKPBtnListener) {
        this.mKPBtnListener = mKPBtnListener;
    }

    public void setRollCallBack(PCBtnListener mPCBtnListener) {
        this.mPCBtnListener = mPCBtnListener;
    }

    public void setInvestigator(Investigator investigator) {
        this.investigator = investigator;
    }

    public void setRecord(Record record) {
        this.record = record;
        sb = new StringBuilder(record.getDetail());
    }

    public Record getRecord(){
        record.setType(Record.TYPE_STORY);
        return record;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_record);
        TextView titleV = findViewById(R.id.tv_show_record_title);
        detailV = findViewById(R.id.tv_show_record_detail);
        detailV.setMovementMethod(new ScrollingMovementMethod());
        titleV.setText(record.getTitle());
        detailV.setText(sb.toString());
        if (type == GameActivity.KP)
            initKPBtn();
        else if (type == GameActivity.PC)
            initPCBtn();
        else
            initNormalBtn();
    }

    private void initTalkBtn(OnSendMsg onSendMsg){
        LinearLayout talkL = findViewById(R.id.ll_show_record_talk);
        talkL.setVisibility(View.VISIBLE);
        Button send = findViewById(R.id.btn_show_record_send);
        EditText msgV = findViewById(R.id.et_show_record_msg);
        send.setOnClickListener(v->{
            String msg = msgV.getText().toString();
            if (msg.isEmpty())
                return;
            onSendMsg.sendMsg(msg);
            msgV.setText("");
        });
    }
    private void initPCBtn(){
        initTalkBtn(mPCBtnListener);
        LinearLayout pcl = findViewById(R.id.ll_show_record_PC);
        pcl.setVisibility(View.VISIBLE);
        Button r,t,s,b,o;
        r = findViewById(R.id.btn_show_record_research);
        t = findViewById(R.id.btn_show_record_talk);
        s = findViewById(R.id.btn_show_record_show);
        b = findViewById(R.id.btn_show_record_battle);
        o = findViewById(R.id.btn_show_record_others);

        r.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), r);
            popup.getMenu().add(0, 1, 0, "观察");
            popup.getMenu().add(0, 2, 0, "聆听");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId()==1)
                    mPCBtnListener.rollSkill("观察",COCUtils.getSkillPointFromI(investigator,7));
                if (item.getItemId()==2)
                    mPCBtnListener.rollSkill("聆听",COCUtils.getSkillPointFromI(investigator,35));
                return false;
            });
            popup.show();
        });
        t.setOnClickListener(v->{
            PopupMenu popup = new PopupMenu(getContext(), t);
            popup.getMenu().add(0, 1, 0, "话术");
            popup.getMenu().add(0, 2, 0, "恐吓");
            popup.getMenu().add(0, 3, 0, "说服");
            popup.getMenu().add(0, 4, 0, "魅惑");
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case 1:
                        mPCBtnListener.rollSkill("话术",COCUtils.getSkillPointFromI(investigator,68));break;
                    case 2:
                        mPCBtnListener.rollSkill("恐吓",COCUtils.getSkillPointFromI(investigator,69));break;
                    case 3:
                        mPCBtnListener.rollSkill("说服",COCUtils.getSkillPointFromI(investigator,70));break;
                    case 4:
                        mPCBtnListener.rollSkill("魅惑",COCUtils.getSkillPointFromI(investigator,71));break;
                }
                return false;
            });
            popup.show();
        });
        s.setOnClickListener(v-> new ShowInvestigatorDialog(getContext(), investigator));
        b.setOnClickListener(v->{
            PopupMenu popup = new PopupMenu(getContext(), b);
            popup.getMenu().add(0, 1, 0, "闪避");
            popup.getMenu().add(0, 2, 0, "格斗");
            popup.getMenu().add(0, 3, 0, "射击");
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case 1:
                        mPCBtnListener.rollSkill("闪避",COCUtils.getSkillPointFromI(investigator,28));break;
                    case 2:
                        sld = new SkillListDialog(getContext(),
                                new SkillRecycleAdapter(COCUtils.selectSkillListByExtra(investigator.getLearnedSkillList(), "格斗"),skill ->{
                                    mPCBtnListener.rollSkill(skill.getName(),skill.getSumPoint());
                                    sld.dismiss();
                                }));
                        sld.show();
                        break;
                    case 3:
                        sld = new SkillListDialog(getContext(),
                                new SkillRecycleAdapter(COCUtils.selectSkillListByExtra(investigator.getLearnedSkillList(), "射击"),skill ->{
                                        mPCBtnListener.rollSkill(skill.getName(),skill.getSumPoint());
                                        sld.dismiss();
                                }));
                        sld.show();
                }
                return false;
            });
            popup.show();
        });
        o.setOnClickListener(v-> {
            PopupMenu popup = new PopupMenu(getContext(), o);
            popup.getMenu().add(0, 1, 0, "已学技能");
            popup.getMenu().add(0, 2, 0, "全部技能");
            SubMenu attSub = popup.getMenu().addSubMenu(0, 3, 0, "属性点");
            attSub.add(1, 1, 0, "力量");
            attSub.add(1, 2, 0, "敏捷");
            attSub.add(1, 3, 0, "意志");
            attSub.add(1, 4, 0, "体质");
            attSub.add(1, 5, 0, "外貌");
            attSub.add(1, 6, 0, "教育");
            attSub.add(1, 7, 0, "体型");
            attSub.add(1, 8, 0, "智力");
            attSub.add(1, 9, 0, "幸运");
            attSub.add(1, 10, 0, "San Check");
            SubMenu cSub = popup.getMenu().addSubMenu(0, 4, 0, "自定义骰子");
            cSub.add(2, 1, 0, "自定义");
            cSub.add(2, 2, 0, "1d100");
            cSub.add(2, 3, 0, "1d20");
            cSub.add(2, 4, 0, "1d10");
            cSub.add(2, 5, 0, "1d8");
            cSub.add(2, 6, 0, "1d6");
            cSub.add(2, 7, 0, "1d4");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getGroupId()==1){
                    int i=item.getItemId();
                    if (i==10)
                        mPCBtnListener.rollSkill("San Check",investigator.getAttributes().getSan());
                    else
                        mPCBtnListener.rollSkill(String.valueOf(item.getTitle()), investigator.getAttributes().getAttAsList().get(i - 1));
                }else if (item.getGroupId() == 2) {
                    if (item.getItemId()==1){
                        iid = new InputInfoDialog(getContext(), (hint, formula) -> {
                            if(formula.matches("(\\d+d\\d+\\+)+\\d?")) {
                                mPCBtnListener.rollCustom(hint, formula);
                                iid.dismiss();
                            }else {
                                ToastUtils.show("表达式不正确");
                            }
                        }, InputInfoDialog.TYPE_DICE);
                        iid.show();
                    }else {
                        mPCBtnListener.rollCustom("快速掷骰", String.valueOf(item.getTitle()));
                    }
                    return false;
                }else if (item.getItemId()==1){
                    sld = new SkillListDialog(getContext(),
                            new SkillRecycleAdapter(investigator.getLearnedSkillList(), skill ->{
                                mPCBtnListener.rollSkill(skill.getName(),skill.getSumPoint());
                                sld.dismiss();
                            }));
                    sld.show();
                }else if (item.getItemId()==2){
                    sld = new SkillListDialog(getContext(),
                            new SkillRecycleAdapter(COCUtils.selectSkillListWithLearned(investigator.getLearnedSkillList()), skill ->{
                                mPCBtnListener.rollSkill(skill.getName(),skill.getSumPoint());
                                sld.dismiss();
                            }));
                    sld.show();
                }
                return false;
            });
            popup.show();
        });
    }
    private void initKPBtn(){
        initTalkBtn(mKPBtnListener);
        LinearLayout kpl = findViewById(R.id.ll_show_record_KP);
        kpl.setVisibility(View.VISIBLE);
        Button finishBtn,darkRollBtn,lightRollBtn;

        finishBtn = findViewById(R.id.btn_show_record_finish);
        darkRollBtn = findViewById(R.id.btn_kp_dark);
        lightRollBtn = findViewById(R.id.btn_kp_light);

        finishBtn.setOnClickListener(v -> mKPBtnListener.onFinish());
        darkRollBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), darkRollBtn);
            popup.getMenu().add(0, 1, 0, "自定义");
            popup.getMenu().add(0, 2, 0, "1d100");
            popup.getMenu().add(0, 3, 0, "1d20");
            popup.getMenu().add(0, 4, 0, "1d10");
            popup.getMenu().add(0, 5, 0, "1d8");
            popup.getMenu().add(0, 6, 0, "1d6");
            popup.getMenu().add(0, 7, 0, "1d4");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId()==1) {
                    iid = new InputInfoDialog(getContext(), (hint, formula) -> {
                        if (formula.matches("(\\d+d\\d+\\+)+\\d?")) {
                            mKPBtnListener.rollDark(hint, formula);
                            iid.dismiss();
                        } else {
                            ToastUtils.show("表达式不正确");
                        }
                    }, InputInfoDialog.TYPE_DICE);
                    iid.show();
                }else {
                    mKPBtnListener.rollDark("快速掷骰", String.valueOf(item.getTitle()));
                }
                return false;
            });
            popup.show();
        });
        lightRollBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), lightRollBtn);
            popup.getMenu().add(0, 1, 0, "自定义");
            popup.getMenu().add(0, 2, 0, "1d100");
            popup.getMenu().add(0, 3, 0, "1d20");
            popup.getMenu().add(0, 4, 0, "1d10");
            popup.getMenu().add(0, 5, 0, "1d8");
            popup.getMenu().add(0, 6, 0, "1d6");
            popup.getMenu().add(0, 7, 0, "1d4");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId()==1) {
                    iid = new InputInfoDialog(getContext(), (hint, formula) -> {
                        if (formula.matches("(\\d+d\\d+\\+)+\\d?")) {
                            mKPBtnListener.roll(hint, formula);
                            iid.dismiss();
                        } else {
                            ToastUtils.show("表达式不正确");
                        }
                    }, InputInfoDialog.TYPE_DICE);
                    iid.show();
                }else {
                    mKPBtnListener.roll("快速掷骰", String.valueOf(item.getTitle()));
                }
                return false;
            });
            popup.show();
        });
    }
    private void initNormalBtn(){
        setCancelable(true);
        Button s,u;
        s = findViewById(R.id.btn_show_record_save);
        u = findViewById(R.id.btn_show_record_upload);

        if (!record.isSaved())
            s.setVisibility(View.VISIBLE);
        if (!record.isUploaded())
            u.setVisibility(View.VISIBLE);

        s.setOnClickListener(v->{
            Story story=new Story();
            story.setTitle(record.getTitle());
            story.setDetail(record.getDetail());
            COCUtils.saveStory(story);
            record.setSaved(true);
            s.setVisibility(View.GONE);
            ToastUtils.show("已保存");
        });
        u.setOnClickListener(v->{
            ProgressDialog progressDialog=new ProgressDialog(getContext());
            progressDialog.setTitle("正在连接服务器……");
            progressDialog.setCancelable(false);
            progressDialog.show();
            NetConnectUtils.requestNet(NetConfig.AddStory,"title="+record.getTitle()+"&detail="+record.getDetail(),
                    new NetConnectUtils.NetCallBack() {
                        @Override
                        public void connectOK(JSONResult result) {
                            if (!result.isOK()){
                                ToastUtils.show("上传失败:"+result.getMessage());
                                progressDialog.dismiss();
                                return;
                            }
                            ToastUtils.show("上传成功!");
                            progressDialog.dismiss();
                            record.setUploaded(true);
                            u.setVisibility(View.GONE);
                        }

                        @Override
                        public void connectFail(String resStr) {
                            progressDialog.dismiss();
                            ToastUtils.show("连接服务器失败"+resStr);
                        }
                    });
        });
    }

    public interface KPBtnListener extends OnSendMsg{
        void onFinish();
        void roll(String hint, String formula);
        void rollDark(String hint, String formula);
    }

    public interface PCBtnListener extends OnSendMsg{
        void rollSkill(String name,int point);
        void rollCustom(String hint,String formula);
    }

    private interface OnSendMsg{
        void sendMsg(String msg);
    }
}
