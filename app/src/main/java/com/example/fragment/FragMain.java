package com.example.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MusicService.MusicService;
import com.example.VolumechangeReceiver.VolumnChangeReceiver;
import com.example.mylatouttest.Lyric.LyricJson;
import com.example.mylatouttest.Lyric.LyricMessageTaker;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class FragMain extends Fragment  {

    private MyApplication myApplication; //全局变量
    int max;  //seekbar的最大值
    ImageButton main_list_bt;
    ImageButton main_play_pause_bt;
    ImageButton main_like_bt;
    ImageButton main_recent_bt;
    ImageButton bottomnext;
    ImageView bottomhead ;
    TextView main_count_tv;
    TextView bottomtitle ;
    TextView bottomsinger;
    TextView lrc;
    SeekBar bottomSeekbar;
    View bottomPlayer;

    ArrayList<Map<String, String>> data = null; //所有歌曲信息
    Thread lyricThread = new Thread() ;  //播歌线程
    String temptitle = "";


    File file ;
    File[] files;

    long time; //再按一次退出计时
    int key = 0;//再按一次退出计数

    private VolumnChangeReceiver volumnChangeReceiver;
    private MessageReceiver messageReceiver;
    private MusicService musicService;
    private WindowManager manager;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MBind) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("info", "service disconnected");
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);

        main_list_bt = (ImageButton) view.findViewById(R.id.main_list_bt);
        main_play_pause_bt = (ImageButton) view.findViewById(R.id.main_play_pause_bt);
        main_like_bt = (ImageButton) view.findViewById(R.id.main_like_bt);
        main_recent_bt = (ImageButton) view.findViewById(R.id.main_recent_bt);
        main_count_tv = (TextView) view.findViewById(R.id.main_count_tv);
        lrc = (TextView) view.findViewById(R.id.lrc);


        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        IntentFilter intentFilter = new IntentFilter();
        messageReceiver = new MessageReceiver();

        intentFilter.addAction("com.example.MusicService.PROGRESS");
        intentFilter.addAction("com.example.MusicService.DETIAL");
        intentFilter.addAction("com.example.LocalMusic.PLAY");
        intentFilter.addAction("CHANGEMAINBUTTON");
        activity.registerReceiver(messageReceiver, intentFilter);

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.main_list_bt
//        }
//    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.example.MusicService.DETIAL")) {
                Log.e("fragmen","recevier");

            } //接受并初始化/修改 当前歌曲 以及歌曲数目 歌词

            if (intent.getAction().equals("com.example.LocalMusic.PLAY")) {
                Log.e("fragmen","recevier");
            }

            if(intent.getAction().equals("CHANGEMAINBUTTON")){
                Log.e("fragmen","recevier");
            }

        }
    }

}
