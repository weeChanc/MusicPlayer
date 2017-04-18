package com.example.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static final String LIKE = "create table Like ("
            +"position integer, "
            +"duration integer, "
            +"singer text, "
            +"title text)";

    public static final String RECENT = "create table Recent ("
            +"position integer, "
            +"duration integer, "
            +"singer text, "
            +"title text)";

    public static final String MYMUSIC = "create table MyMusic ("
            +"position integer primary key autoincrement, "
            +"duration integer, "
            +"singer text, "
            +"data text, "
            +"fulltitle text, "
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
