package com.hosigus.coc_helper.items;

/**
 * Created by 某只机智 on 2018/2/16.
 */

public class Story {
    public static final int CHOOSE_NULL=0;
    public static final int CHOOSE_LIKE=1;
    public static final int CHOOSE_EMBAR=2;

    private int id;
    private String title;
    private String detail;
    private int likeNum;
    private int embarrassNum;
    private int choose = CHOOSE_NULL;

    public int getChoose() {
        return choose;
    }

    public void setChoose(int choose) {
        this.choose = choose;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getEmbarrassNum() {
        return embarrassNum;
    }

    public void setEmbarrassNum(int embarrassNum) {
        this.embarrassNum = embarrassNum;
    }
}
