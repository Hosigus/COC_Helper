package com.hosigus.coc_helper.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hosigus.coc_helper.MyApplication;

import java.util.Scanner;

import static com.hosigus.coc_helper.configs.SQLConfig.*;

/**
 * Created by 某只机智 on 2018/1/29.
 */

public class COCSQLHelper extends SQLiteOpenHelper {
    public COCSQLHelper() {
        super(MyApplication.getContext(),NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SKILL);
        db.execSQL(CREATE_TABLE_PROFESSION);
        db.execSQL(CREATE_TABLE_PROFESSION_SKILL);
        db.execSQL(CREATE_TABLE_PLAYER_INFO);
        db.execSQL(CREATE_TABLE_PLAYER_SKILL);
        db.execSQL(CREATE_TABLE_PLAYER_ATTRIBUTES);
        db.execSQL(CREATE_TABLE_STORY_LIST);
        db.execSQL(CREATE_TABLE_STORY_CHOOSE);

        initData(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void initData(SQLiteDatabase db){
        db.beginTransaction();
        Scanner in;
        try {
            in=new Scanner(MyApplication.getContext().getClass().getClassLoader().getResourceAsStream("assets/init_skill"));
            while (in.hasNextLine()){
                String strLine=in.nextLine();
                String strItems[]=strLine.split(",");
                ContentValues values=new ContentValues();
                values.put("id",Integer.parseInt(strItems[0]));
                values.put("name",strItems[1]);
                if (!strItems[2].isEmpty())
                    values.put("extra", strItems[2]);
                values.put("start_point",Integer.parseInt(strItems[3]));
                db.insert("skill",null,values);
            }
            in.close();
            in=new Scanner(MyApplication.getContext().getClass().getClassLoader().getResourceAsStream("assets/init_profession"));
            while (in.hasNextLine()){
                String strLine=in.nextLine();
                String strItems[]=strLine.split(",");
                ContentValues values=new ContentValues();
                values.put("id",Integer.parseInt(strItems[0]));
                values.put("name",strItems[1]);
                values.put("credit",strItems[2]);
                values.put("attribute",strItems[3]);
                db.insert("profession",null,values);
            }
            in.close();

            in=new Scanner(MyApplication.getContext().getClass().getClassLoader().getResourceAsStream("assets/init_profession_skill"));
            while (in.hasNextLine()){
                String strLine=in.nextLine();
                String strItems[]=strLine.split(",");
                int profession_id=Integer.parseInt(strItems[0]);
                String strSkills[]=strItems[1].split(";");
                for (String skill:strSkills
                        ) {
                    String singleSkill[] = skill.split("\\.");
                    ContentValues values = new ContentValues();
                    values.put("profession_id", profession_id);
                    values.put("skill_id", Integer.parseInt(singleSkill[0]));
                    if (singleSkill.length>1)
                        values.put("skill_extra", singleSkill[1]);
                    db.insert("profession_skill", null, values);
                }
            }
            in.close();
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }
}
