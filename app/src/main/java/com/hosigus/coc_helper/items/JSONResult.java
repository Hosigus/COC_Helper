package com.hosigus.coc_helper.items;

import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by 某只机智 on 2018/2/16.
 */

public class JSONResult {
    private int status;
    private String message;
    private JSONObject data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public boolean isOK() {
        return status == HttpURLConnection.HTTP_OK;
    }
}
