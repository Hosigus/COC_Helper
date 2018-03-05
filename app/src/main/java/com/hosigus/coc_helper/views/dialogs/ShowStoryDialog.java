package com.hosigus.coc_helper.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.StoryRecycleAdapter;
import com.hosigus.coc_helper.configs.NetConfig;
import com.hosigus.coc_helper.fragments.StoryFragment;
import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.utils.DensityUtils;
import com.hosigus.coc_helper.utils.NetConnectUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

/**
 * Created by 某只机智 on 2018/2/20.
 */

public class ShowStoryDialog extends Dialog {
    private Story mStory;
    private CallBack callBack;

    private TextView likeTV;
    private TextView embarrassTV;

    public ShowStoryDialog(@NonNull Context context, Story mStory) {
        super(context);
        this.mStory = mStory;
        callBack=null;
        DensityUtils.setScreenFullHeight(context,getWindow(),16);
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
    }

    public ShowStoryDialog(@NonNull Context context, Story mStory, CallBack callBack) {
        super(context);
        this.mStory = mStory;
        this.callBack = callBack;
        DensityUtils.setScreenFullHeight(context,getWindow(),16);
        DensityUtils.setScreenFullWidth(context,getWindow(),32);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_story);

        TextView titleTV = findViewById(R.id.tv_show_story_title);
        TextView detailTV = findViewById(R.id.tv_show_story_detail);
        likeTV = findViewById(R.id.tv_show_story_like);
        embarrassTV = findViewById(R.id.tv_show_story_embarrass);

        if (callBack!=null){
            likeTV.setOnClickListener(v1 -> {
                if (mStory.getChoose()==Story.CHOOSE_EMBAR)
                    return;
                if (mStory.getChoose()==Story.CHOOSE_LIKE)
                    tryCancelLike();
                else
                    tryLike();
            });
            embarrassTV.setOnClickListener(v1 -> {
                if (mStory.getChoose()==Story.CHOOSE_LIKE)
                    return;
                if (mStory.getChoose()==Story.CHOOSE_EMBAR)
                    tryCancelEmbar();
                else
                    tryEmbar();
            });
            refreshLE();
        }else {
            likeTV.setVisibility(View.GONE);
            embarrassTV.setVisibility(View.GONE);
        }

        detailTV.setMovementMethod(ScrollingMovementMethod.getInstance());

        titleTV.setText(mStory.getTitle());
        detailTV.setText(mStory.getDetail());
    }

    private void refreshLE(){
        int likeN=mStory.getLikeNum(),embarN=mStory.getEmbarrassNum();
        likeTV.setText(String.valueOf(likeN));
        embarrassTV.setText(String.valueOf(embarN));

        float likeW,embarW;
        int max=(int)Math.pow(10,String.valueOf(Math.max(likeN,embarN)).length());
        likeW=max-embarN;
        embarW=max-likeN;
        if (likeW/embarW>3){
            likeW=3;
            embarW=1;
        }else if (embarW/likeW>3){
            embarW=3;
            likeW=1;
        }
        likeTV.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,likeW));
        embarrassTV.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,embarW));

        if (mStory.getChoose()==Story.CHOOSE_LIKE)
            likeTV.setBackgroundResource(R.drawable.background_btn_like_dark);
        else
            likeTV.setBackgroundResource(R.drawable.background_btn_like_light);

        if (mStory.getChoose()==Story.CHOOSE_EMBAR)
            embarrassTV.setBackgroundResource(R.drawable.background_btn_embarrass_dark);
        else
            embarrassTV.setBackgroundResource(R.drawable.background_btn_embarrass_light);
    }
    private void tryCancelLike(){
        mStory.setLikeNum(mStory.getLikeNum()-1);
        refreshLE();
        NetConnectUtils.requestNet(NetConfig.LOrEStory,
                "story_id="+ mStory.getId()+"&to="+NetConfig.LIKE_STORY+"&flag=-1",
                new NetConnectUtils.NetCallBack() {
                    @Override
                    public void connectOK(JSONResult result) {
                        if (!result.isOK())
                            connectFail(result.getMessage());
                        else {
                            mStory.setChoose(Story.CHOOSE_NULL);
                            callBack.onChoose(mStory.getId(),mStory.getChoose());
                            refreshLE();
                        }
                    }
                    @Override
                    public void connectFail(String resStr) {
                        ToastUtils.show("失败："+resStr);
                        mStory.setLikeNum(mStory.getLikeNum()+1);
                        refreshLE();
                    }
                });
    }
    private void tryCancelEmbar(){
        mStory.setEmbarrassNum(mStory.getEmbarrassNum()-1);
        refreshLE();
        NetConnectUtils.requestNet(NetConfig.LOrEStory,
                "story_id="+ mStory.getId()+"&to="+NetConfig.EMBARRASS_STORY+"&flag=-1",
                new NetConnectUtils.NetCallBack() {
                    @Override
                    public void connectOK(JSONResult result) {
                        if (!result.isOK())
                            connectFail(result.getMessage());
                        else{
                            mStory.setChoose(Story.CHOOSE_NULL);
                            callBack.onChoose(mStory.getId(),mStory.getChoose());
                            refreshLE();
                        }
                    }
                    @Override
                    public void connectFail(String resStr) {
                        ToastUtils.show("失败："+resStr);
                        mStory.setEmbarrassNum(mStory.getEmbarrassNum()+1);
                        refreshLE();
                    }
                });
    }
    private void tryLike(){
        mStory.setLikeNum(mStory.getLikeNum()+1);
        refreshLE();
        mStory.setChoose(Story.CHOOSE_LIKE);
        NetConnectUtils.requestNet(NetConfig.LOrEStory,
                "story_id="+ mStory.getId()+"&to="+NetConfig.LIKE_STORY+"&flag=1",
                new NetConnectUtils.NetCallBack() {
                    @Override
                    public void connectOK(JSONResult result) {
                        if (!result.isOK())
                            connectFail(result.getMessage());
                        else{
                            callBack.onChoose(mStory.getId(),mStory.getChoose());
                            refreshLE();
                        }
                    }
                    @Override
                    public void connectFail(String resStr) {
                        ToastUtils.show("失败："+resStr);
                        mStory.setLikeNum(mStory.getLikeNum()-1);
                        mStory.setChoose(Story.CHOOSE_NULL);
                        refreshLE();
                    }
                });
    }
    private void tryEmbar() {
        mStory.setEmbarrassNum(mStory.getEmbarrassNum()+1);
        refreshLE();
        mStory.setChoose(Story.CHOOSE_EMBAR);
        NetConnectUtils.requestNet(NetConfig.LOrEStory,
                "story_id="+ mStory.getId()+"&to="+NetConfig.EMBARRASS_STORY+"&flag=1",
                new NetConnectUtils.NetCallBack() {
                    @Override
                    public void connectOK(JSONResult result) {
                        if (!result.isOK())
                            connectFail(result.getMessage());
                        else{
                            callBack.onChoose(mStory.getId(),mStory.getChoose());
                            refreshLE();
                        }
                    }
                    @Override
                    public void connectFail(String resStr) {
                        ToastUtils.show("失败："+resStr);
                        mStory.setEmbarrassNum(mStory.getEmbarrassNum()-1);
                        mStory.setChoose(Story.CHOOSE_NULL);
                        refreshLE();
                    }
                });
    }

    public interface CallBack{
        void onChoose(int id, int choose);
    }
}
