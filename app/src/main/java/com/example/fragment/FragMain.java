package com.example.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.MusicService.MusicService;
import com.example.VolumechangeReceiver.VolumnChangeReceiver;

import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;

import java.util.ArrayList;

import java.util.Map;


/**
 * Created by 铖哥 on 2017/4/10.
 */

public class FragMain extends Fragment {


    int max;  //seekbar的最大值
    ImageButton main_list_bt;
    ImageButton main_play_pause_bt;
    ImageButton main_like_bt;
    ImageButton main_recent_bt;
    ImageButton bottomnext;
    ImageView bottomhead;
    TextView main_count_tv;
    TextView bottomtitle;
    TextView bottomsinger;
    TextView lrc;
    SeekBar bottomSeekbar;
    View bottomPlayer;

    ArrayList<Map<String, String>> data = null; //所有歌曲信息
    Thread lyricThread = new Thread();  //播歌线程
    String temptitle = "";
    MyApplication myApplication = MyApplication.getApplication();//全局变量

    File file;
    File[] files;

    long time; //再按一次退出计时
    int key = 0;//再按一次退出计数

    private VolumnChangeReceiver volumnChangeReceiver;
    private MessageReceiver messageReceiver;
    private MusicService musicService;
    private WindowManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        main_recent_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) (getActivity())).fragRecent();
            }
        });


        main_like_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) (getActivity())).fragLike();
            }
        });

        main_list_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) (getActivity())).fragLocal();
            }
        });

        main_play_pause_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                Intent intentnotify1 = new Intent("com.example.MusicService.NOTIFI");
                if (myApplication.isPlay()) {
                    myApplication.setIsPlay(false);
                    ((MainActivity) (getActivity())).stopMusic();
                    getActivity().sendBroadcast(intentnotify1);
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);
                } else {
                    myApplication.setIsPlay(true);
                    intent.putExtra("ISPAUSE", true);
                    getActivity().sendBroadcast(intent);
                    getActivity().sendBroadcast(intentnotify1);
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                }
            }
        });

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.example.MusicService.DETIAL")) {
                main_count_tv.setText(intent.getIntExtra("COUNT", 0) + "");
                Log.e("fragmen", "recevier");

            } //接受并初始化/修改 当前歌曲 以及歌曲数目 歌词

            if (intent.getAction().equals("com.example.LocalMusic.PLAY")) {
                Log.e("fragmen", "recevier");
            }

            if (intent.getAction().equals("CHANGEMAINBUTTON")) {
                Log.e("fragmen", "recevier");
            }

            //接受并初始化/修改 当前歌曲 以及歌曲数目 歌词

            if (intent.getAction().equals("com.example.LocalMusic.PLAY")) {
                myApplication.setIsPlay(true);
                main_play_pause_bt.setImageResource(R.drawable.pausewhite);
            }

            if (intent.getAction().equals("CHANGEMAINBUTTON")) {
                if (myApplication.isPlay()) {
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                } else
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);
            }

        }
    }

}
