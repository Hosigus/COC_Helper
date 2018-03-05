package com.hosigus.coc_helper.items;

import com.hosigus.coc_helper.utils.COCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 某只机智 on 2018/2/21.
 */

public class Profession {
    private int id;
    private String name;
    private String credit;
    private String attribute;
    private Map<String, List<Skill>> skills;

    public Profession() {
    }

    public Profession(int id, String name, String credit, String attribute) {
        this.id = id;
        this.name=name;
        this.credit = credit;
        this.attribute = attribute;
        skills= COCUtils.selectSkillsFromProfession(id);
    }

    public boolean isProSkill(Skill skill){
        return isProSkill(skill, new ArrayList<>());
    }

    public boolean isProSkill(Skill skill,List<Skill> selectedList){
        if (id==1)
            return true;

        int sid=skill.getId();
        if(sid==1)
            return true;
        else if (isNormalProSkill(sid))
            return true;
        else if (skill.getExtra()==null)
            return false;
        else if (skills.containsKey(skill.getExtra())){
            if (selectedList.size()==0)
                return true;
            int sdN=0;
            for (Skill sdSkill:selectedList) {
                if (sdSkill.getId()==skill.getId())
                    return true;
                if (sdSkill.getExtra()==null)
                    continue;
                if (sdSkill.getExtra().equals(skill.getExtra())&&(!isNormalProSkill(sdSkill.getId()))){
                    sdN++;
                }
            }
            if (sdN<=skills.get(skill.getExtra()).size())
                return true;
        }

        return false;
    }

    private boolean isNormalProSkill(int id){
        List<Skill> skillList = skills.get("normal");
        for (Skill s : skillList) {
            if (s.getId() == id)
                return true;
        }
        return false;
    }

    public String getSkillsString(){
        if (id==1)
            return "自定义。注：请在使用自定义职业前咨询你的守秘人";

        StringBuilder sb = new StringBuilder();
        List<Skill> tempList = skills.get("normal");
        if (tempList!=null){
            for (Skill skill :tempList) {
                sb.append(skill.getName()).append("、");
            }
        }
        for (String groupName:skills.keySet()) {
            if (groupName.equals("normal")||groupName.equals("null")){
                continue;
            }
            sb.append(skills.get(groupName).size()).append("个").append(groupName).append("类技能、");
        }
        tempList = skills.get("null");
        if (tempList!=null){
            sb.append(tempList.size()).append("个任选技能、");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, List<Skill>> getSkills() {
        return skills;
    }

    public String getCredit() {
        return credit;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setSkills(Map<String, List<Skill>> skills) {
        this.skills = skills;
    }
}
