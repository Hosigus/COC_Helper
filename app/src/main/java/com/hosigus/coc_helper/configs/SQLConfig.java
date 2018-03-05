package com.hosigus.coc_helper.configs;

/**
 * Created by 某只机智 on 2018/1/29.
 */

public class SQLConfig {
    public static final int VERSION=1;
    public static final String NAME="COC";
    public static final String CREATE_TABLE_SKILL=//预设技能表
            "CREATE TABLE skill (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(30) NOT NULL," +
                    "extra VARCHAR(30) DEFAULT NULL," +
                    "start_point INTEGER NOT NULL" +
                    ")";

    public static final String CREATE_TABLE_PROFESSION=//预设职业表
            "CREATE TABLE profession (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(30) NOT NULL UNIQUE," +
                    "credit VARCHAR(10) NOT NULL," +
                    "attribute VARCHAR(50) NOT NULL" +
                    ")";

    public static final String CREATE_TABLE_PROFESSION_SKILL=//预设职业&技能表
            "CREATE TABLE profession_skill (" +
                    "profession_id INTEGER NOT NULL," +
                    "skill_id INTEGER NOT NULL," +
                    "skill_extra VARCHAR(30) DEFAULT NULL," +
                    "FOREIGN KEY(profession_id) REFERENCES profession(id)," +
                    "FOREIGN KEY(skill_id) REFERENCES skill(id)" +
                    ")";

    public static final String CREATE_TABLE_PLAYER_INFO=
            "CREATE TABLE player_info (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name       VARCHAR(10)    NOT NULL," +
                    "decade     VARCHAR(10) DEFAULT '现代'," +
                    "profession_id INTEGER DEFAULT 0," +
                    "age INTEGER DEFAULT 22," +
                    "sex CHAR(2) DEFAULT '人妖'," +
                    "homeland VARCHAR(20) DEFAULT '中国'," +
                    "address VARCHAR(20) DEFAULT '中国'," +
                    "FOREIGN KEY(profession_id) REFERENCES profession(id)" +
                    ")";

    public static final String CREATE_TABLE_PLAYER_SKILL=
            "CREATE TABLE player_skill (" +
                    "player_id INTEGER NOT NULL," +
                    "skill_id INTEGER NOT NULL," +
                    "extra VARCHAR(30) DEFAULT NULL," +
                    "int_point INTEGER DEFAULT 0," +
                    "pro_point INTEGER DEFAULT 0," +
                    "FOREIGN KEY(player_id) REFERENCES player_info(id)," +
                    "FOREIGN KEY(skill_id) REFERENCES skill(id)" +
                    ")";

    public static final String CREATE_TABLE_PLAYER_ATTRIBUTES=
            "CREATE TABLE player_attributes (" +
                    "player_id INTEGER PRIMARY KEY NOT NULL ," +
                    "str INTEGER NOT NULL," +
                    "dex INTEGER NOT NULL," +
                    "pow INTEGER NOT NULL," +
                    "con INTEGER NOT NULL," +
                    "app INTEGER NOT NULL," +
                    "edu INTEGER NOT NULL," +
                    "siz INTEGER NOT NULL," +
                    "int INTEGER NOT NULL," +
                    "life INTEGER NOT NULL," +
                    "san INTEGER NOT NULL," +
                    "luck INTEGER NOT NULL," +
                    "magic INTEGER NOT NULL," +
                    "state CHAR(10) NOT NULL DEFAULT '神志清醒'," +
                    "FOREIGN KEY(player_id) REFERENCES player_info(id)"+
                    ")";

    public static final String CREATE_TABLE_STORY_LIST=
            "CREATE TABLE story_list (" +
                    "story_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "title VARCHAR(30) NOT NULL," +
                    "detail TEXT NOT NULL" +
                    ")";

    public static final String CREATE_TABLE_STORY_CHOOSE =
            "CREATE TABLE story_choose (" +
                    "story_id INTEGER PRIMARY KEY NOT NULL ," +
                    "choose INTEGER NOT NULL" +
                    ")";
}
