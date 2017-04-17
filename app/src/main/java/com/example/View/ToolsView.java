package com.example.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 铖哥 on 2017/4/9.
 */

public class ToolsView extends LinearLayout {


    public static final int ORDER = 1;
    public static final int RANDOM = 2;
    public static final int LOOP = 3;


    MyApplication myApplication = MyApplication.getApplication();
    ImageButton mode_bt;
    int mode = myApplication.getPlay_mode();
    Context context;

    public ToolsView(final Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.tools, this);

        mode_bt = (ImageButton) view.findViewById(R.id.mode_bt);
        mode_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode();
            }
        });

        if (mode == ORDER) {
            mode_bt.setImageResource(R.drawable.ic_order);
        } else if (mode == RANDOM) {
            mode_bt.setImageResource((R.drawable.ic_random));
        } else if (mode == LOOP) {
            mode_bt.setImageResource((R.drawable.ic_loop));      //初始化图标
        }




    }

    void setMode() {
        SharedPreferences.Editor editor = context.getSharedPreferences("data", MODE_PRIVATE).edit();

        if(mode == LOOP) mode = 0 ;    //处理 LOOP的情况
        ++mode;
        Log.e("tag",mode+"");
        if (mode == ORDER) {
            mode_bt.setImageResource(R.drawable.ic_order);
        } else if (mode == RANDOM) {
            mode_bt.setImageResource((R.drawable.ic_random));
        } else if (mode == LOOP) {
            mode_bt.setImageResource((R.drawable.ic_loop));      //设置模式
        }
        myApplication.setPlay_mode(mode);
        editor.putInt("MODE", mode);  //按下即保存
        editor.apply();
    }
}
