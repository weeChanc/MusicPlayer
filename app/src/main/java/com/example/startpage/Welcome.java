package com.example.startpage;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.MyAdapter.WelcomePagerAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 导航界面 第一次启动程序的时候执行
 * 使用ViewPager实现导航页面
 */

public class Welcome extends AppCompatActivity {
    ImageView image;
    private ArrayList<Map<String, String>> data = new ArrayList<>();
    private ArrayList<Map<String, String>> finaldata = new ArrayList<>();
    MyApplication myApplication = MyApplication.getApplication();
    SQLiteDatabase db = myApplication.getDp();
    int temp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        temp++;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (temp == 1)
                    readMusicData();
            }
        }).start();


        image = (ImageView) findViewById(R.id.point);

        ViewPager viewPager = (ViewPager) findViewById(R.id.welcome);
        viewPager.setOffscreenPageLimit(3);  //由于默认的ViewPager只缓存2张图片(加显示中的一张) 设置成这样 为缓存3张图片(加显示中的一张) 实现循环左右滑动。

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //该方法在页面跳转后得到调用
                if (position == 0) {
                    image.setImageResource(R.drawable.ic_one);
                } else if (position == 1) {
                    image.setImageResource(R.drawable.ic_two);
                } else {
                    image.setImageResource(R.drawable.ic_three);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);        //三页导航
        View wel1 = inflater.inflate(R.layout.welcome1, null);
        View wel2 = inflater.inflate(R.layout.welcome2, null);
        View wel3 = inflater.inflate(R.layout.welcome3, null);
        ArrayList<View> arrayList = new ArrayList<>();

        arrayList.add(wel1);
        arrayList.add(wel2);
        arrayList.add(wel3);

        WelcomePagerAdapter welcomeAdapter = new WelcomePagerAdapter(arrayList, this);
        viewPager.setAdapter(welcomeAdapter);

    }

    private void readMusicData() {

        String[] want = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION};

        if (myApplication.getData() != null) {
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, want, MediaStore.Audio.Media.DURATION + ">60000", null, MediaStore.Audio.Media.TITLE);
            if (cursor != null && cursor.moveToFirst()) {            //读取时间大于一分钟的歌曲  并且按照歌名排序
                do {

                    Map<String, String> map = new HashMap<>();
                    map.put("title", cursor.getString(0));       //歌曲标题
                    map.put("data", cursor.getString(1));        //歌曲路径              //读取音乐文件
                    map.put("singer", cursor.getString(2));      //歌手名
                    map.put("duration", cursor.getInt(4) + "");  //歌曲长度
                    data.add(map);
                    finaldata.add(map);

                    ContentValues values = new ContentValues();
                    values.put("title", map.get("title"));
                    values.put("data", map.get("data"));
                    values.put("singer", map.get("singer"));
                    values.put("duration", Integer.parseInt(map.get("duration")));
                    db.insert("MyMusic", null, values);  //导入到自己的数据库


                } while (cursor.moveToNext());


                cursor.close();

            }
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

}
