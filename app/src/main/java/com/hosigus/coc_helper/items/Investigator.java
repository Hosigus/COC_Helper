package com.hosigus.coc_helper.items;

import java.io.Serializable;
import java.util.List;

/**
 * 这是调查员类
 */

public class Investigator implements Serializable{
    private Profession profession;
    private List<Skill> learnedSkillList;
    private Attributes attributes;
    private int id;
    private int age;
    private String name;
    private String decade;
    private String homeland;
    private String address;
    private String sex;

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public List<Skill> getLearnedSkillList() {
        return learnedSkillList;
    }

    public void setLearnedSkillList(List<Skill> learnedSkillList) {
        this.learnedSkillList = learnedSkillList;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDecade() {
        return decade;
    }

    public void setDecade(String decade) {
        this.decade = decade;
    }

    public String getHomeland() {
        return homeland;
    }

    public void setHomeland(String homeland) {
        this.homeland = homeland;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
