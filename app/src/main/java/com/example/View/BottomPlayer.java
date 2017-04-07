package com.example.View;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylatouttest.R;

/**
 * Created by 铖哥 on 2017/4/7.
 */

public class BottomPlayer extends RelativeLayout {
    SeekBar seekbar;

    public BottomPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.bottomplayer,this);

         seekbar = (SeekBar)findViewById(R.id.seekBar);
         ImageButton NEXT = (ImageButton)findViewById(R.id.NEXT);
         TextView tv = (TextView)findViewById(R.id.tv);

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
            }
        });

    }
}
