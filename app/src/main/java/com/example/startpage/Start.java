package com.example.startpage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.dataBase.MyDataBaseHelper;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 起始加载页面 用于加载音乐歌曲数据 创建歌曲/
 * 创建数据库 存放我喜欢列表 以及最近播放列表数据/
 * 读取喜欢的歌曲的列表/
 * 歌词文件夹/检查权限并申请权限/
 * 恢复退出时歌曲播放模式
 */

public class Start extends Activity {

    MyApplication myApplication;
    File file;
    private ArrayList<Map<String, String>> data = new ArrayList<>();
    private ArrayList<Map<String, String>> finaldata = new ArrayList<>();
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Start.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }                     //申请运行时权限
        if( ContextCompat.checkSelfPermission(Start.this,Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(Start.this,new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
        }

        while (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) ;

        myApplication = (MyApplication) getApplication();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        MyDataBaseHelper dpHelper = new MyDataBaseHelper(this, "list.db", null, 3);
        SQLiteDatabase dp = dpHelper.getWritableDatabase();
        myApplication.setDp(dp);
        imageView = (ImageView) findViewById(R.id.im);
        imageView.setImageResource(R.drawable.ic_start);


        initTask task = new initTask();
        task.execute();                                              //执行线程

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);                //隐藏状态栏
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    private class initTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            long startTime = System.currentTimeMillis();   //时间用于规定该页面最小停留时间避免过快退出造成卡顿/不适感

            String directory = Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() + "/MyLyric/";

            file = new File(directory);
            if (!file.exists())         //创建本地音乐/歌词播放文件夹
                file.mkdir();

            myApplication.setFile(file);

            readMusicData();
            readLovePos();          //读取数据
//            readPlayTime();

            myApplication.setData(data);

            while ((System.currentTimeMillis() - startTime) < 1000) ;     //一秒后换图标

            publishProgress();                                      // 让progressbar 消失变成 新图标

            while ((System.currentTimeMillis() - startTime) < 500) ;     //并 让更改的图标 停留0.5秒

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //播放模式(0,1,2)   public static final int ORDER = =1
            SharedPreferences share = getSharedPreferences("data", MODE_PRIVATE);       // public static final int RANDOM = 2;public static final int LOOP = 3;
            int mode = share.getInt("MODE", -1);                                       // 0实际上为 3 LOOP 因为自增原因将其变成0
            if (mode == -1) {
                myApplication.setPlay_mode(MyApplication.ORDER);
                Intent intent = new Intent("android.intent.action.WELCOME");
                intent.addCategory("android.intent.category.DEFAULT");              //根据播放模式是否存在 (0,1,2)判断是否为第一次打开播放器 若是第一次则  开始导航页面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);                   //打开活动后将本活动出栈，避免返回的时候，回到该活动
                startActivity(intent);
                share.edit().putInt("MODE", MyApplication.ORDER).apply();
            } else {

                myApplication.setPlay_mode(mode);                                    //存在 则设置模式 并启动打开 活动
                Intent intent = new Intent("android.intent.action.MAINMUSIC");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);                     //打开活动后将本活动出栈，避免返回的时候，回到该活动
                intent.addCategory("android.intent.category.DEFAULT");
                startActivity(intent);
            }
            finish();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            RelativeLayout yes = (RelativeLayout) findViewById(R.id.yes);
            progressBar.setVisibility(View.GONE);
            yes.setBackgroundResource(R.drawable.ic_yes);                      //更换图标 提示加载完毕

        }
    }

    private void readMusicData() {

       SQLiteDatabase db =  myApplication.getDp();
        Cursor cursor = db.query("MyMusic",null,null,null,null,null,null);
        if (cursor.moveToFirst() && cursor != null) {            //读取时间大于一分钟的歌曲  并且按照歌名排序
            do {
                Map<String, String> map = new HashMap<>();
                map.put("title", cursor.getString(cursor.getColumnIndex("title")));       //歌曲标题
                map.put("data", cursor.getString(cursor.getColumnIndex("data")));        //歌曲路径              //读取音乐文件
                map.put("singer", cursor.getString(cursor.getColumnIndex("singer")));      //歌手名
                map.put("fulltitle", cursor.getString(cursor.getColumnIndex("fulltitle")));   //歌手名+歌曲名
                map.put("duration", cursor.getInt(cursor.getColumnIndex("duration"))+"");  //歌曲长度
                data.add(map);
                finaldata.add(map);

            } while (cursor.moveToNext());
            cursor.close();

        }

        for (int i = 0; i < data.size(); i++) {
            data.get(i).put("position", i + "");            //为每首歌曲标记绝对位置
            finaldata.get(i).put("position", i + "");
        }
        /**
         *    该data为音乐播放的时候选择路径的唯一一个数据源
         *    每个表的数据库都包含对应歌曲在此data中的position
         *    在不同列表中点击item项播放歌曲的时候
         *    就可以根据该position选择对应的歌曲播放
         */

        myApplication.setData(data);
        myApplication.setFinaldata(finaldata);
    }

    private void readLovePos() {

        ArrayList<Integer> pos = myApplication.getPos();
        SQLiteDatabase db = myApplication.getDp();

        Cursor cursor = db.query("Like", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                pos.add(cursor.getInt(cursor.getColumnIndex("position")));          //添加我喜欢的歌曲到Arraylist中
            } while (cursor.moveToNext());
        }

        cursor.close();
    }


}


