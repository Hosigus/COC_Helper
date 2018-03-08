package com.hosigus.coc_helper.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.hosigus.coc_helper.items.Attributes;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.items.Profession;
import com.hosigus.coc_helper.items.Skill;
import com.hosigus.coc_helper.items.Story;
import com.hosigus.coc_helper.views.dialogs.InveListDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hosigus.coc_helper.activities.WelcomeActivity.db;

/**
 * Created by 某只机智 on 2018/2/22.
 */

public class COCUtils {

    /**
     * 按照 职业ID 从数据库加载对应职业的技能表
     *
     * @param professionId 职业id
     * @return skill的map表
     * 其中String为分组名,两部分：
     * 1.'normal'组,存ID!=1,指定了ID的职业(!=1 , ..)
     * 2."extra" 组,存ID=1,指定了分组的职业(1,..) [组名为extra信息,而其中任选职业(1,)组为'null']
     * List为该分组内skill
     */
    public static Map<String, List<Skill>> selectSkillsFromProfession(int professionId) {
        Map<String, List<Skill>> skillMap = new HashMap<>();

        Cursor cursor =
                db.rawQuery(
                        "SELECT skill.id,skill.name,profession_skill.skill_extra,skill.start_point " +
                                "FROM profession_skill " +
                                "INNER JOIN skill " +
                                "ON profession_skill.skill_id = skill.id " +
                                "WHERE profession_skill.profession_id = ? "
                        , new String[]{String.valueOf(professionId)});

        // 0-id,1-name,2-extra,3-point
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String extra = cursor.getString(2);

                String groupName;
                if (id != 1) {
                    groupName = "normal";
                } else if (extra != null) {
                    groupName = extra;
                } else {
                    groupName = "null";
                }

                List<Skill> list = skillMap.get(groupName);
                if (list == null)
                    list = new ArrayList<>();

                list.add(new Skill(id,
                        cursor.getString(1),
                        extra,
                        cursor.getInt(3)));

                skillMap.put(groupName, list);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return skillMap;
    }

    /**
     * 解析职业点
     *
     * @param attributes   从该param中获取属性值
     * @param attributeStr 需要解析的Str
     * @return 算出的值
     */
    public static int parserSkillPoint(Attributes attributes, String attributeStr) {
        int sum = 0;
        String attName = null;
        for (int i = 0, length = attributeStr.length(); i < length; i++) {
            switch (attributeStr.charAt(i)) {
                case '*':
                    sum += attributes.getValueFromStr(attName) * Integer.parseInt(attributeStr.charAt(i + 1) + "");
                    i++;
                    break;
                case '+':
                    if (!Character.isDigit(attributeStr.charAt(i - 1)))
                        sum += attributes.getValueFromStr(attName);
                    break;
                case '|':
                    String newName = attributeStr.substring(i + 1, i + 4);
                    i += 3;
                    attName = attributes.getValueFromStr(newName) > attributes.getValueFromStr(attName) ? newName : attName;
                    break;
                default:
                    attName = attributeStr.substring(i, i + 3);
                    i += 2;
            }
        }
        return sum;
    }

    public static List<Skill> selectSkillListWithoutList(List<Integer> skillIdList) {
        List<Skill> skillList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM `skill`", new String[]{});
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id"),
                    nameIndex = cursor.getColumnIndex("name"),
                    extraIndex = cursor.getColumnIndex("extra"),
                    startIndex = cursor.getColumnIndex("start_point");
            do {
                int id = cursor.getInt(idIndex);
                if (skillIdList.contains(id))
                    continue;
                skillList.add(new Skill(id, cursor.getString(nameIndex), cursor.getString(extraIndex), cursor.getInt(startIndex)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return skillList;
    }

    public static void saveInvestigator(Investigator i, CallBack callBack) {
        Boolean state = false;
        ContentValues infoV = new ContentValues();
        infoV.put("name", i.getName());
        infoV.put("decade", i.getDecade());
        infoV.put("profession_id", i.getProfession().getId());
        infoV.put("age", i.getAge());
        infoV.put("sex", i.getSex());
        infoV.put("homeland", i.getHomeland());
        infoV.put("address", i.getAddress());
        ContentValues attV = new ContentValues();
        db.insert("player_info", null, infoV);
        Cursor cursor = db.rawQuery("SELECT id FROM player_info ORDER BY id DESC LIMIT 1 ", new String[]{});
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        Attributes att = i.getAttributes();
        attV.put("player_id", id);
        attV.put("str", att.getStr());
        attV.put("dex", att.getDex());
        attV.put("pow", att.getPow());
        attV.put("con", att.getCon());
        attV.put("app", att.getApp());
        attV.put("edu", att.getEdu());
        attV.put("siz", att.getSiz());
        attV.put("int", att.getInt());
        attV.put("life", att.getLifeMax());
        attV.put("san", att.getPow());
        attV.put("luck", att.getLuck());
        attV.put("magic", att.getMagicMax());
        db.beginTransaction();
        try {
            db.insert("player_attributes", null, attV);
            List<Skill> skillList = i.getLearnedSkillList();
            for (Skill s : skillList) {
                ContentValues skillV = new ContentValues();
                skillV.put("player_id", id);
                skillV.put("skill_id", s.getId());
                if (s.getExtra() != null)
                    skillV.put("extra", s.getExtra());
                skillV.put("int_point", s.getIntPoint());
                skillV.put("pro_point", s.getProPoint());
                db.insert("player_skill", null, skillV);
            }
            db.setTransactionSuccessful();
            state = true;
        } finally {
            db.endTransaction();
            callBack.onSaveEnd(state);
        }
        FileUtils.renameFile("temp", i.getName() + id);
    }

    public static Investigator selectInvestigatorById(int id) {
        Investigator i = new Investigator();
        Cursor cursor = db.rawQuery("SELECT * FROM player_info WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            i.setId(id);
            i.setName(cursor.getString(cursor.getColumnIndex("name")));
            i.setDecade(cursor.getString(cursor.getColumnIndex("decade")));
            Profession p = new Profession();
            p.setId(cursor.getInt(cursor.getColumnIndex("profession_id")));
            Cursor c = db.rawQuery("SELECT name FROM profession WHERE id = ?", new String[]{String.valueOf(p.getId())});
            c.moveToFirst();
            p.setName(c.getString(0));
            c.close();
            i.setProfession(p);
            i.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            i.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            i.setHomeland(cursor.getString(cursor.getColumnIndex("homeland")));
            i.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            Attributes att = selectAttById(id);
            i.setAttributes(att);
            i.setLearnedSkillList(selectSkillListByIId(id, att));
        }
        cursor.close();
        return i;
    }

    public static Attributes selectAttById(int id) {
        Attributes att;
        Cursor cursor = db.rawQuery("SELECT * FROM player_attributes WHERE player_id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        List<Integer> list = new ArrayList<>();
        list.add(cursor.getInt(cursor.getColumnIndex("str")));
        list.add(cursor.getInt(cursor.getColumnIndex("dex")));
        list.add(cursor.getInt(cursor.getColumnIndex("pow")));
        list.add(cursor.getInt(cursor.getColumnIndex("con")));
        list.add(cursor.getInt(cursor.getColumnIndex("app")));
        list.add(cursor.getInt(cursor.getColumnIndex("edu")));
        list.add(cursor.getInt(cursor.getColumnIndex("siz")));
        list.add(cursor.getInt(cursor.getColumnIndex("int")));
        list.add(cursor.getInt(cursor.getColumnIndex("luck")));
        att = new Attributes(list);
        att.setSan(cursor.getInt(cursor.getColumnIndex("san")));
        att.setMagic(cursor.getInt(cursor.getColumnIndex("magic")));
        att.setLife(cursor.getInt(cursor.getColumnIndex("life")));
        att.setState(cursor.getString(cursor.getColumnIndex("state")));
        cursor.close();
        return att;
    }

    public static List<Investigator> selectAllInvestigatorAsList() {
        List<Investigator> iList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM player_info", new String[]{});
        if (cursor.moveToFirst()) {
            int iIndex = cursor.getColumnIndex("id"),
                    nIndex = cursor.getColumnIndex("name"),
                    aIndex = cursor.getColumnIndex("age"),
                    sIndex = cursor.getColumnIndex("sex"),
                    pIndex = cursor.getColumnIndex("profession_id"),
                    dIndex = cursor.getColumnIndex("decade"),
                    hIndex = cursor.getColumnIndex("homeland"),
                    addIndex = cursor.getColumnIndex("address");
            do {
                Investigator i = new Investigator();
                i.setId(cursor.getInt(iIndex));
                i.setName(cursor.getString(nIndex));
                i.setDecade(cursor.getString(dIndex));
                Profession p = new Profession();
                p.setId(cursor.getInt(pIndex));
                Cursor c = db.rawQuery("SELECT name FROM profession WHERE id = ?", new String[]{String.valueOf(p.getId())});
                c.moveToFirst();
                p.setName(c.getString(0));
                c.close();
                i.setProfession(p);
                i.setAge(cursor.getInt(aIndex));
                i.setSex(cursor.getString(sIndex));
                i.setHomeland(cursor.getString(hIndex));
                i.setAddress(cursor.getString(addIndex));
                Attributes att = selectAttById(i.getId());
                i.setAttributes(att);
                i.setLearnedSkillList(selectSkillListByIId(i.getId(), att));
                iList.add(i);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return iList;
    }

    public static List<Skill> selectSkillListByIId(int id, Attributes att) {
        Cursor cursor = db.rawQuery(
                "SELECT skill.id,skill.name,skill.extra,skill.start_point," +
                        "player_skill.int_point,player_skill.pro_point " +
                        "FROM player_skill " +
                        "INNER JOIN skill " +
                        "ON player_skill.skill_id = skill.id " +
                        "WHERE player_skill.player_id = ? "
                , new String[]{String.valueOf(id)});
        List<Skill> skillList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int startP = cursor.getInt(3), skillId = cursor.getInt(0);
                if (startP == -1) {
                    if (skillId == 28)
                        startP = att.getDex() / 2;
                    else if (skillId == 96)
                        startP = att.getEdu();
                }
                Skill skill = new Skill(skillId, cursor.getString(1), cursor.getString(2), startP);
                skill.setIntPoint(cursor.getInt(4));
                skill.setProPoint(cursor.getInt(5));
                skillList.add(skill);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return skillList;
    }

    public static int getSkillPointFromI(Investigator i, int skillId) {
        List<Skill> lsList = i.getLearnedSkillList();
        for (Skill s : lsList) {
            if (s.getId() == skillId)
                return s.getSumPoint();
        }
        Cursor cursor = db.rawQuery("SELECT start_point FROM skill WHERE id =?", new String[]{String.valueOf(skillId)});
        cursor.moveToFirst();
        int p = cursor.getInt(0);
        cursor.close();
        return p;
    }

    public static List<Skill> selectSkillListByExtra(List<Skill> learned,String extra){
        List<Skill> skillList = new ArrayList<>();
        List<Skill> usefulSL = new ArrayList<>();
        for (Skill s :learned) {
            String sextra = s.getExtra();
            if (sextra!=null&&s.getExtra().equals(extra)){
                usefulSL.add(s);
            }
        }
        Cursor cursor = db.rawQuery("SELECT * FROM skill WHERE extra = ?", new String[]{extra});
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id"),
                    nameIndex = cursor.getColumnIndex("name"),
                    extraIndex = cursor.getColumnIndex("extra"),
                    startIndex = cursor.getColumnIndex("start_point");
            do {
                int id = cursor.getInt(idIndex);
                Skill as = new Skill(id, cursor.getString(nameIndex), cursor.getString(extraIndex), cursor.getInt(startIndex));
                for (Skill s :usefulSL) {
                    if (s.getId()==id){
                        as.setIntPoint(s.getIntPoint());
                        as.setProPoint(s.getProPoint());
                    }
                }
                skillList.add(as);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return skillList;
    }

    public static void saveStory(Story story) {
        ContentValues values = new ContentValues();
        values.put("title", story.getTitle());
        values.put("detail", story.getDetail());
        db.insert("story_list", null, values);
    }

    public static List<Skill> selectSkillListWithLearned(List<Skill> learned) {
        List<Skill> skillList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM skill", new String[]{});
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id"),
                    nameIndex = cursor.getColumnIndex("name"),
                    extraIndex = cursor.getColumnIndex("extra"),
                    startIndex = cursor.getColumnIndex("start_point");
            do {
                int id = cursor.getInt(idIndex);
                Skill as = new Skill(id, cursor.getString(nameIndex), cursor.getString(extraIndex), cursor.getInt(startIndex));
                for (Skill s :learned) {
                    if (s.getId()==id){
                        as.setIntPoint(s.getIntPoint());
                        as.setProPoint(s.getProPoint());
                    }
                }
                skillList.add(as);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return skillList;
    }

    public static List<Integer> parserDice(String str) {
        // TODO: 2018/3/3
        Matcher matcher = Pattern.compile("(\\d+d\\d+\\+)+\\d+").matcher(str);
        if(matcher.find()&&matcher.groupCount()==1) {
            return null;
        }else return null;
    }

    public static void saveAttChange(Attributes att,int id) {
        ContentValues values = new ContentValues();
        values.put("life", att.getLife());
        values.put("san", att.getSan());
        values.put("magic", att.getMagic());
        values.put("state", att.getState());
        db.update("player_attributes", values, "player_id = ?", new String[]{String.valueOf(id)});
    }

    public interface CallBack {
        void onSaveEnd(Boolean state);
    }
}
