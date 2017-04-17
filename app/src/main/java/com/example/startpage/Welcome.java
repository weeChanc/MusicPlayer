package com.example.startpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.MyAdapter.WelcomePagerAdapter;
import com.example.mylatouttest.R;

import java.util.ArrayList;

public class Welcome extends AppCompatActivity {
    ImageView image;
    Receiver receiver = new Receiver();

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MAINMUSIC");
        registerReceiver(receiver,intentFilter);

        image = (ImageView) findViewById(R.id.point);

        ViewPager viewPager = (ViewPager) findViewById(R.id.welcome);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

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

        LayoutInflater inflater = LayoutInflater.from(this);
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

    class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
