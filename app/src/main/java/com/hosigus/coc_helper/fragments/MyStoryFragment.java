package com.hosigus.coc_helper.fragments;

import android.database.Cursor;

import com.hosigus.coc_helper.configs.Settings;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hosigus.coc_helper.activities.WelcomeActivity.db;

/**
 * Created by 某只机智 on 2018/2/13.
 * 主页面page中的home页的“我的”页面
 */

public class MyStoryFragment extends StoryFragment{
    @Override
    void refreshStory() {
        adapter.setIsShowLike(false);
        Cursor cursor=db.rawQuery("SELECT * FROM story_list ORDER BY story_id DESC LIMIT ? OFFSET ?",
                new String[]{String.valueOf(Settings.storyNumInPage),"0"});
        List<Story> storyList=new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Story story=new Story();
                story.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
                story.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                story.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                storyList.add(story);
            }while (cursor.moveToNext());
        }else {
            setEmpty();
        }
        cursor.close();
        onRefreshEnd(storyList);
    }

    @Override
    void loadStroy() {
        Cursor cursor=db.rawQuery("SELECT * FROM story_list ORDER BY story_id DESC LIMIT ? OFFSET ?",
                new String[]{String.valueOf(Settings.storyNumInPage),String.valueOf(adapter.getItemCount())});
        List<Story> storyList=new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Story story=new Story();
                story.setId(cursor.getInt(cursor.getColumnIndex("story_id")));
                story.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                story.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
            }while (cursor.moveToNext());
        }else {
            ToastUtils.show("下面……下面没有了哦");
        }
        cursor.close();
        onLoadEnd(storyList);
    }
}
