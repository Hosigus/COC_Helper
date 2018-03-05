package com.hosigus.coc_helper.configs;

/**
 * Created by 某只机智 on 2018/2/16.
 */

public class NetConfig {
    public static final String GetStoryList = "http://coc.api.hosigus.tech/GetStory.php";
    public static final String AddStory = "http://coc.api.hosigus.tech/AddStory.php";
    public static final String LOrEStory = "http://coc.api.hosigus.tech/LOrEStory.php";

    public static final String HOST = "159.203.169.45";
    public static final int PORT = 1112;

    public static final int EMBARRASS_STORY = -1;
    public static final int LIKE_STORY = 1;

    public static final int HEART_BEAT_RATE = 3 * 1000;
    public static final int NOT_RESPONSE_LIMIT = HEART_BEAT_RATE * 10;

    public static final String HB_STR = "{\"type\":1}";
    public static final String CLOSE_STR = "{\"type\":7}";
    public static final String END_STR = "{\"type\":9}";

    public static final int HB = 1;
    public static final int CREATE = 2;
    public static final int BAN = 3;
    public static final int KICK = 4;
    public static final int ENTER = 5;
    public static final int ROLL = 6;
    public static final int CLOSE = 7;
    public static final int START = 8;
    public static final int END = 9;

    public static final int ROLL_CUSTOM = 1;
    public static final int ROLL_SKILL = 2;
    public static final int ROLL_CUSTOM_KP = 3;
}
