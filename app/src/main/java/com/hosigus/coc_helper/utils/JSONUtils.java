package com.hosigus.coc_helper.utils;

import android.util.Log;

import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.items.Story;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/16.
 * Parsor
 */

public class JSONUtils {
    /**
     * 解析 Sting 为 mStory
     * @param data 原数据一次解析的‘data’数据
     * @return 解析完成的 mStory ,如果解析出错，则返回 null
     */
    public static List<Story> parserToStories(JSONObject data){
        List<Story> storyList=new ArrayList<>();
        try {
            JSONArray array=data.getJSONArray("stories");
            for (int i = 0,length=array.length(); i < length; i++) {
                Story story=new Story();
                JSONObject json = array.getJSONObject(i);
                story.setId(json.getInt("id"));
                story.setTitle(json.getString("title"));
                story.setDetail(json.getString("detail"));
                story.setLikeNum(json.getInt("like_num"));
                story.setEmbarrassNum(json.getInt("embarrass_num"));
                storyList.add(story);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return storyList;
    }
    static JSONResult parserJSON(String jsonStr){
        JSONObject json;
        JSONResult result=new JSONResult();
        try {
            json=new JSONObject(jsonStr);
            Log.d("Test", "parserJSON: "+jsonStr);
            result.setStatus(json.getInt("status"));
            result.setMessage(json.getString("message"));
            result.setData(json.getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
            result.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
            result.setMessage("未知错误");
        }
        return result;
    }
    public static String parserStr(JSONObject json,String param){
        String result;
        try {
            result = json.getString(param);
        } catch (JSONException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
}
