package com.example.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static final String LIKE = "create table Like ("
            +"id integer primary key autoincrement, "
            +"duration integer, "
            +"singer text, "
            +"title text, "
            +"position integer)";


    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LIKE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
