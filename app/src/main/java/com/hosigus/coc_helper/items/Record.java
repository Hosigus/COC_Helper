package com.hosigus.coc_helper.items;

/**
 * Created by 某只机智 on 2018/2/27.
 */

public class Record {
    public static final int TYPE_NOTICE = 1;
    public static final int TYPE_STORY = 2;
    private String detail;
    private String title;
    private int type;
    private boolean isSaved = false;
    private boolean isUploaded = false;

    public int getType() {
        return type;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getDetail() {
        return detail;
    }

    public String getTitle() {
        return title;
    }
}
