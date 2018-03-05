package com.hosigus.coc_helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.configs.NetConfig;
import com.hosigus.coc_helper.configs.Settings;
import com.hosigus.coc_helper.fragments.StoryFragment;
import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.utils.NetConnectUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/16.
 * StoryRecycleAdapter
 */

public class StoryRecycleAdapter extends RecyclerView.Adapter<StoryRecycleAdapter.StoryHolder> {
    private List<Story> storyList;
    private StoryFragment.CallBack mCallBack;
    private Boolean isShowLike;

    private static final String TAG = "StoryRecycleAdapterTest";

    public StoryRecycleAdapter(StoryFragment.CallBack mCallBack) {
        storyList=new ArrayList<>();
        this.mCallBack = mCallBack;
        isShowLike=true;
    }

    public void setIsShowLike(Boolean isShowLike){
        this.isShowLike=isShowLike;
    }

    public Story getStory(int position){
        return storyList.get(position);
    }

    public void addStory(List<Story> newStoryList){
        storyList.addAll(newStoryList);
        notifyDataSetChanged();
    }

    public void refreshStory(List<Story> newStoryList){
        storyList.clear();
        storyList.addAll(newStoryList);
        notifyDataSetChanged();
    }

    public void updateStory(Story newStory,int index){
        storyList.set(index,newStory);
        notifyItemChanged(index);
    }

    @Override
    public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story,parent,false));
    }

    @Override
    public void onBindViewHolder(StoryHolder holder, int position) {
        holder.setStory(storyList.get(position));
        holder.v.setOnClickListener((v1 -> mCallBack.onShowStory(storyList.get(position),isShowLike,position)));
        int count = getItemCount();
        if (count > Settings.storyNumInPage && position==count-1)
            mCallBack.onLoad();
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    class StoryHolder extends RecyclerView.ViewHolder {
        Story mStory;
        TextView titleTV;
        TextView detailTV;
        TextView likeTV;
        TextView embarrassTV;
        View v;

        StoryHolder(View v) {
            super(v);
            this.v=v;
            titleTV=v.findViewById(R.id.tv_item_story_title);
            detailTV = v.findViewById(R.id.tv_item_story_detail);
            likeTV = v.findViewById(R.id.tv_item_story_like);
            embarrassTV = v.findViewById(R.id.tv_item_story_embarrass);
            if (isShowLike){
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
            }
        }

        void setStory(Story story){
            mStory=story;
            if (isShowLike){
                refreshLE();
            }else {
                detailTV.setLines(5);
                likeTV.setVisibility(View.GONE);
                embarrassTV.setVisibility(View.GONE);
            }
            titleTV.setText(story.getTitle());
            detailTV.setText(story.getDetail());
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
                                mCallBack.onSaveChoose(mStory.getId(),mStory.getChoose());
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
                                mCallBack.onSaveChoose(mStory.getId(),mStory.getChoose());
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
                            mCallBack.onSaveChoose(mStory.getId(),mStory.getChoose());
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
                                mCallBack.onSaveChoose(mStory.getId(),mStory.getChoose());
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
    }
}
