package com.example.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

/**
 * Created by 铖哥 on 2017/4/7.
 */

public class BottomPlayer extends RelativeLayout {

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.example.MusicService.PROGRESS")){
                seekbar.setProgress(intent.getIntExtra("PROGRESS",0));
            }
        }
    }

    MyReceiver myReceiver = new MyReceiver();
    MyApplication myApplication = MyApplication.getApplication();
    SeekBar seekbar;



    public BottomPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.bottomplayer, this);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        ImageButton NEXT = (ImageButton) findViewById(R.id.NEXT);
        TextView tv = (TextView) findViewById(R.id.tv);

        registerMyReceiver();

        seekbar.setMax(myApplication.getSeekBarMax());
        tv.setText(myApplication.getBottomTitle());

        NEXT.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seekbar.setProgress(0);
                Intent intentnext = new Intent("com.example.MainActivity.STARTMUSIC");
                intentnext.putExtra("NEXT", true);
                getContext().sendBroadcast(intentnext);

                Intent intentnotify = new Intent("com.example.MusicService.NOTIFI");
                intentnotify.putExtra("PLAY", true);
                getContext().sendBroadcast(intentnotify);

                Toast.makeText(getContext(), myApplication.isSeekBarTouch()+"", Toast.LENGTH_SHORT).show();
            }
        });



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("info", "asd");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {     //按下进度条 先调用onStartTrackingTouch一次，再调用onProgressChanged一次
//                seekBar.setMax(myApplication.getSeekBarMax());
                if (!myApplication.isPlay()) {
                     myApplication.setIsPlay(true);
                }
                myApplication.setIsSeekBarTouch(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent2 = new Intent("com.example.MainActivity.STARTMUSIC");
                intent2.putExtra("PROGRESS", seekBar.getProgress() );
                getContext().sendBroadcast(intent2);
                myApplication.setIsSeekBarTouch(false);
            }
        });

    }
    void registerMyReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.MusicService.PROGRESS");
        getContext().registerReceiver(myReceiver,intentFilter);
    }
}


