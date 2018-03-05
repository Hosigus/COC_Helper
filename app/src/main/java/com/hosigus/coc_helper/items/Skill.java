package com.hosigus.coc_helper.items;

/**
 * Created by 某只机智 on 2018/2/21.
 */

public class Skill {
    private int id;
    private int startPoint;
    private int proPoint;
    private int intPoint;
    private String name;
    private String extra;

    public Skill(int id) {
        this.id = id;
        initSkill();
    }

    public Skill(int id, String name, String extra, int startPoint) {
        this.id = id;
        this.startPoint = startPoint;
        this.name = name;
        this.extra = extra;
        proPoint = 0;
        intPoint = 0;
    }

    private void initSkill() {
    }

    public void setProPoint(int proPoint) {
        this.proPoint = proPoint;
    }

    public void setIntPoint(int intPoint) {
        this.intPoint = intPoint;
    }

    public int getId() {
        return id;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public int getProPoint() {
        return proPoint;
    }

    public int getIntPoint() {
        return intPoint;
    }

    public String getName() {
        return name;
    }

    public String getExtra() {
        return extra;
    }

    public int getSumPoint() {
        return startPoint+proPoint+intPoint;
    }

}
