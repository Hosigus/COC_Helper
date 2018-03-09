package com.hosigus.coc_helper.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.StoryRecycleAdapter;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.views.dialogs.ShowStoryDialog;

import java.util.List;

import static com.hosigus.coc_helper.activities.WelcomeActivity.db;

/**
 * Created by 某只机智 on 2018/2/17.
 * {@link MyStoryFragment} 和 {@link HotStoryFragment} 的父类
 */

public abstract class StoryFragment extends Fragment {
    StoryRecycleAdapter adapter;
    private LinearLayout loadingLayout;
    private SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayout emptyHint;

    /**
     * 初始化数据
     */
    private void initAdapter(){
        adapter=new StoryRecycleAdapter(new CallBack() {
            @Override
            public void onLoad() {
                loadingLayout.setVisibility(View.VISIBLE);
                loadStroy();
            }

            @Override
            public void onSaveChoose(int id, int choose) {
                ContentValues values=new ContentValues();
                values.put("choose",choose);
                db.update("story_choose",values,"story_id = ?",new String[]{String.valueOf(id)});
            }

            @Override
            public void onShowStory(Story story, boolean isShowLE,int position) {
                ShowStoryDialog storyDialog;
                if (isShowLE){
                   storyDialog = new ShowStoryDialog(getContext(), story, (id,choose) -> {
                       ContentValues values=new ContentValues();
                       values.put("choose",choose);
                       db.update("story_choose",values,"story_id = ?",new String[]{String.valueOf(id)});
                       story.setChoose(choose);
                       adapter.updateStory(story,position);
                   });
                }else
                    storyDialog=new ShowStoryDialog(getContext(),story);
                storyDialog.show();

            }
        });
        refreshStory();
    }

    /**
     * 从数据库中加载/添加Story的Choose
     * @param storyList 未添加的List
     * @return 添加完的List
     */
    List<Story> setChoose(List<Story> storyList){
        for (Story story:storyList) {
            Cursor cursor=db.rawQuery("SELECT choose FROM story_choose WHERE story_id = ?",
                    new String[]{String.valueOf(story.getId())});
            if (cursor.moveToFirst()){
                story.setChoose(cursor.getInt(cursor.getColumnIndex("choose")));
            }else {
                ContentValues values=new ContentValues();
                values.put("story_id",story.getId());
                values.put("choose",Story.CHOOSE_NULL);
                db.insert("story_choose",null,values);
            }
            cursor.close();
        }
        return storyList;
    }

    /**
     * 刷新/重载数据
     * 结束需调用onRefreshEnd(..);
     */
    abstract void refreshStory();

    void onRefreshEnd(List<Story> storyList){
        if (storyList!=null){
            adapter.refreshStory(storyList);
            if (adapter.getItemCount()==0)
                emptyHint.setVisibility(View.VISIBLE);
            else
                emptyHint.setVisibility(View.GONE);
        }
        onRefreshEnd();
    }

    void setEmpty(){
        emptyHint.setVisibility(View.VISIBLE);
    }

    void onRefreshEnd(){
        if (emptyHint.isRefreshing())
            emptyHint.setRefreshing(false);
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

    /**
     * 加载更多数据
     * 结束需调用onLoadEnd(..);
     */
    abstract void loadStroy();

    void onLoadEnd(List<Story> storyList){
        if (storyList==null)
            onLoadEnd();
        adapter.addStory(storyList);
        loadingLayout.setVisibility(View.GONE);
    }

    void onLoadEnd(){
        loadingLayout.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_home_page_story,container,false);
        refreshLayout = v.findViewById(R.id.srl_story);
        refreshLayout.setOnRefreshListener(this::refreshStory);
        loadingLayout = v.findViewById(R.id.ll_story_load);
        emptyHint = v.findViewById(R.id.hint_empty);
        emptyHint.setOnRefreshListener(this::refreshStory);

        initAdapter();

        RecyclerView recyclerView = v.findViewById(R.id.rv_story);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        return v;
    }
    public interface CallBack {
        void onLoad();
        void onSaveChoose(int id, int choose);
        void onShowStory(Story story,boolean isShowLE,int position);
    }
}
