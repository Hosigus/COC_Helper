package com.hosigus.coc_helper.items;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/23.
 */

public class Attributes {
    private int str;
    private int dex;
    private int pow;
    private int con;
    private int app;
    private int edu;
    private int siz;
    private int Int;
    private int luck;
    private int san;
    private int life;
    private int magic;
    private String state;

    public int getValueFromStr(String attStr){
        if (attStr.equals("str"))
            return str;
        else if (attStr.equals("dex"))
            return dex;
        else if (attStr.equals("pow"))
            return pow;
        else if (attStr.equals("con"))
            return con;
        else if (attStr.equals("app"))
            return app;
        else if (attStr.equals("edu"))
            return edu;
        else if (attStr.equals("siz"))
            return siz;
        else if (attStr.equals("int"))
            return Int;
        else if (attStr.equals("life"))
            return life;
        else if (attStr.equals("san"))
            return san;
        else if (attStr.equals("luck"))
            return luck;
        else if (attStr.equals("magic"))
            return magic;
        return 0;
    }

    public Attributes(List<Integer> attList){
        str = attList.get(0);
        dex = attList.get(1);
        pow = attList.get(2);
        con = attList.get(3);
        app = attList.get(4);
        edu = attList.get(5);
        siz = attList.get(6);
        Int = attList.get(7);
        luck = attList.get(8);
    }

    public List<Integer> getAttAsList(){
        List<Integer> attList = new ArrayList<>();
        attList.add(str);
        attList.add(dex);
        attList.add(pow);
        attList.add(con);
        attList.add(app);
        attList.add(edu);
        attList.add(siz);
        attList.add(Int);
        attList.add(luck);
        return attList;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getPow() {
        return pow;
    }

    public void setPow(int pow) {
        this.pow = pow;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public int getEdu() {
        return edu;
    }

    public void setEdu(int edu) {
        this.edu = edu;
    }

    public int getSiz() {
        return siz;
    }

    public void setSiz(int siz) {
        this.siz = siz;
    }

    public int getMov(int age) {
//        DEX 与 STR 皆小于 SIZ：MOV 7
//        DEX 或 STR 其中之一等于或大于 SIZ，或皆等于
//        SIZ： MOV 8
//        STR 与 DEX 皆大于 SIZ: MOV9
//        年龄在 40-49 岁之间：MOV 减少 1
//        年龄在 50-59 岁之间：MOV 减少 2
//        年龄在 60-69 岁之间：MOV 减少 3
//        年龄在 70-79 岁之间：MOV 减少 4
//        年龄在 80-89 岁之间：MOV 减少 5

        int mov;

        if (dex>siz){
            if (str>siz)
                mov=9;
            else
                mov=8;
        }else if (str>siz)
            mov=8;
        else
            mov=7;

        int level=(age-40)/10;
        if (level>=0)
            mov-=level+1
                    ;
        return mov;
    }

    public int getInt() {
        return Int;
    }

    public void setInt(int anInt) {
        Int = anInt;
    }

    public int getLife() {
        return life;
    }

    public int getLifeMax(){
        return (siz+con)/10;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getSan() {
        return san;
    }

    public int getSanMax(){
        return pow;
    }

    public void setSan(int san) {
        this.san = san;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getMagicMax() {
        return pow/5;
    }
}
