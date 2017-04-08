package com.example.local_music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.MyAdapter.MySimpleAdapter;
import com.example.MyAdapter.ViewPagerAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalMusic extends AppCompatActivity implements View.OnClickListener {

    String mode = "orl";
    ArrayList<Map<String, String>> musicdata = null;
    ImageButton local_mode_bt;
    ArrayList<Map<String, Object>> data;
    MyApplication myApplication;




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localmusic);
        myApplication = (MyApplication) getApplication();

        musicdata = myApplication.getData();

        local_mode_bt = (ImageButton) findViewById(R.id.local_mode_bt);
        local_mode_bt.setOnClickListener(this);
        insertDesign();

        Toolbar toolbar = (Toolbar) findViewById(R.id.local_toolbar);
        ActionBar actionBar;
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * 文件 储存 恢复 操作
         */
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
            mode = sharedPreferences.getString("MODE", "rlo");
            if (mode.charAt(2) == 'o') local_mode_bt.setImageResource((R.drawable.orderplay));
            if (mode.charAt(2) == 'r') local_mode_bt.setImageResource((R.drawable.randomblue));
            if (mode.charAt(2) == 'l') local_mode_bt.setImageResource((R.drawable.loopplaybule));

            Intent modeintent = new Intent("com.example.LocalMusic.MODE");
            modeintent.putExtra("MODE", mode.charAt(2));
            sendBroadcast(modeintent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("info", "didnt' find");
        }

        Log.e("mode", mode + "");

//        DataReceiver dataReceiver = new DataReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.example.MusicService.ARRAYLIST");
//        registerReceiver(dataReceiver,intentFilter);

        Intent intent = getIntent();
        ArrayList arrayList = intent.getStringArrayListExtra("data");

        ListView listView = (ListView) findViewById(R.id.local_music_listview);

        data = new ArrayList<>();
        MySimpleAdapter simpleAdapter = new MySimpleAdapter(this, musicdata, R.layout.listitem,
                new String[]{"title","singer"}, new int[]{R.id.local_list_title,R.id.local_SingerName});
        listView.setAdapter(simpleAdapter);



        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.pagerdownlist,null);
        View view2 = inflater.inflate(R.layout.pagerdownlist,null);
        ListView listview1 = (ListView)view1.findViewById(R.id.PAGE_DOWN);
        ListView listview2 = (ListView)view2.findViewById(R.id.PAGE_DOWN);
        listview1.setAdapter(simpleAdapter);
        listview2.setAdapter(simpleAdapter);
        listview1.setDivider(null);
        listview2.setDivider(null);

        List<View> listviewa = new ArrayList<>();

        listviewa.add(view1);
        listviewa.add(view2);

        ArrayList<String> list = new ArrayList<>();
        list.add("第一页");list.add("第二页");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(listviewa,list);

        ViewPager viewpager = (ViewPager)findViewById(R.id.pager);
        viewpager.setAdapter(viewPagerAdapter);


    }


    @Override
    protected void onDestroy() {

        SharedPreferences.Editor editor = this.getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("MODE", mode);
        editor.apply();

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_mode_bt:
                setMode();
                break;
        }

    }

    void setMode() {
        Intent modeintent = new Intent("com.example.LocalMusic.MODE");
        if (mode.charAt(0) == 'o') {
            local_mode_bt.setImageResource(R.drawable.orderplay);
            modeintent.putExtra("MODE", 'o');
            sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'o';
        } else if (mode.charAt(0) == 'r') {
            local_mode_bt.setImageResource((R.drawable.randomblue));
            modeintent.putExtra("MODE", 'r');
            sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'r';
        } else if (mode.charAt(0) == 'l') {
            local_mode_bt.setImageResource((R.drawable.loopplaybule));
            modeintent.putExtra("MODE", 'l');
            sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'l';
        }


    }

    private void insertDesign() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;            //隐藏导航栏
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);                //隐藏状态栏
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
