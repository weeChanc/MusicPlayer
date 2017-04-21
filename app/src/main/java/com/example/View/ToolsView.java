package com.example.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.util.zip.Inflater;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 铖哥 on 2017/4/9.
 */


/**
 * 自定义控件用于每个Fragment中共用的布局 即控制播放模式的布局
 * 每按一次改变播放的模式
 */
public class ToolsView extends LinearLayout {


    public static final int ORDER = 1;  //顺序
    public static final int RANDOM = 2; //随机
    public static final int LOOP = 3;   //循环


    MyApplication myApplication = MyApplication.getApplication();
    ImageButton mode_bt;
    ImageButton delete;
    ImageButton deleteEnsure;
    CheckBox checkBox;
    int mode = myApplication.getPlay_mode();
    Context context;

    public ToolsView(final Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.tools, this);

        Receiver receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ChangeToolsButton");
        context.registerReceiver(receiver, intentFilter);

        mode_bt = (ImageButton) view.findViewById(R.id.mode_bt);
        deleteEnsure = (ImageButton) view.findViewById(R.id.delete_ensure);
        checkBox = (CheckBox) view.findViewById(R.id.delall);


        mode_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode();
            }
        });

        delete = (ImageButton) view.findViewById(R.id.delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myApplication.isDeleteAll()) {
                    myApplication.setDeleteAll(false);
                    deleteEnsure.setVisibility(GONE);
                    checkBox.setVisibility(GONE);
                } else {
                    myApplication.setDeleteAll(true);
                    deleteEnsure.setVisibility(VISIBLE);
                    checkBox.setVisibility(VISIBLE);
                    checkBox.setChecked(false);
                }
                Intent intent = new Intent("ShowOrHideCheckBox");
                context.sendBroadcast(intent);

            }
        });

        deleteEnsure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("DeleteEnsure");
                if (checkBox.isChecked()) {
                    intent.putExtra("deleteFile", true);
                }
                context.sendBroadcast(intent);
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
        if (mode == LOOP) mode = 0;    //处理 LOOP的情况
        ++mode;

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

    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ChangeToolsButton")) {

                deleteEnsure.setVisibility(GONE);
                checkBox.setVisibility(GONE);
                checkBox.setChecked(false);
            }

        }

    }

}
