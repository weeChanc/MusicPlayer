package com.example.startpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.MyAdapter.WelcomePagerAdapter;
import com.example.mylatouttest.R;

import java.util.ArrayList;

/**
 *  导航界面 第一次启动程序的时候执行
 *  使用ViewPager实现导航页面
 */

public class Welcome extends AppCompatActivity {
    ImageView image;

    @Override
    protected void onDestroy() {
        Log.e("destory","destory");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

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
                if(position == 0 ){
                    image.setImageResource(R.drawable.ic_one);
                }else
                if(position == 1){
                    image.setImageResource(R.drawable.ic_two);
                }else{
                    image.setImageResource(R.drawable.ic_three);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);        //三页导航
        View wel1 = inflater.inflate(R.layout.welcome1,null);
        View wel2 = inflater.inflate(R.layout.welcome2,null);
        View wel3 = inflater.inflate(R.layout.welcome3,null);
        ArrayList<View> arrayList = new ArrayList<>();

        arrayList.add(wel1);
        arrayList.add(wel2);
        arrayList.add(wel3);

        WelcomePagerAdapter welcomeAdapter = new WelcomePagerAdapter(arrayList,this);
        viewPager.setAdapter(welcomeAdapter);

    }

}
