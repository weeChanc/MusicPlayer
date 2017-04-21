package com.example.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static final String LIKE = "create table Like (" //喜欢列表
            +"duration integer, "
            +"singer text, "
            +"data text, "
            +"title text)";

    public static final String RECENT = "create table Recent ("  //最近播放列表
            +"duration integer, "
            +"singer text, "
            +"data text, "
            +"title text)";

    public static final String MYMUSIC = "create table MyMusic (" //本地音乐列表 系统读取存入自身数据库
            +"duration integer, "
            +"singer text, "
            +"data text, "
            +"title text)";

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LIKE);
        db.execSQL(RECENT);
        db.execSQL(MYMUSIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Book");
        db.execSQL("drop table if exists Category");
        onCreate(db);

    }
}
