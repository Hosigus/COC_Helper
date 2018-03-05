package com.hosigus.coc_helper.fragments;

import android.content.ContentValues;
import android.database.Cursor;

import com.hosigus.coc_helper.configs.NetConfig;
import com.hosigus.coc_helper.configs.Settings;
import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.utils.JSONUtils;
import com.hosigus.coc_helper.utils.NetConnectUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

import java.util.List;

/**
 * Created by 某只机智 on 2018/2/13.
 * 主页面page中的home页的“热门”页面
 */

public class HotStoryFragment extends StoryFragment{

    @Override
    void refreshStory() {
        NetConnectUtils.requestNet(NetConfig.GetStoryList,"num="+ Settings.storyNumInPage, new NetConnectUtils.NetCallBack() {
            @Override
            public void connectOK(JSONResult result) {
                if (!result.isOK()){
                    ToastUtils.show(result.getMessage());
                    onRefreshEnd();
                }else {
                    onRefreshEnd(setChoose(JSONUtils.parserToStories(result.getData())));
                }
            }
            @Override
            public void connectFail(String resStr) {
                ToastUtils.show("刷新失败:"+resStr);
                onRefreshEnd();
            }
        });
    }

    @Override
    void loadStroy() {
        NetConnectUtils.requestNet(NetConfig.GetStoryList,"position="+adapter.getItemCount()+"&num="+ Settings.storyNumInPage, new NetConnectUtils.NetCallBack() {
            @Override
            public void connectOK(JSONResult result) {
                if (!result.isOK()){
                    ToastUtils.show(result.getMessage());
                    onLoadEnd();
                }else {
                    onLoadEnd(setChoose(JSONUtils.parserToStories(result.getData())));
                }
            }
            @Override
            public void connectFail(String resStr) {
                ToastUtils.show("加载失败:"+resStr);
                onLoadEnd();
            }
        });
    }
}
