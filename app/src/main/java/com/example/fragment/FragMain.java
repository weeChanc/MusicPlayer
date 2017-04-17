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


    ImageButton main_list_bt;
    ImageButton main_play_pause_bt;
    ImageButton main_like_bt;
    ImageButton main_recent_bt;
    ImageButton main_search_bt;
    TextView main_count_tv;
    MyApplication myApplication = MyApplication.getApplication();//全局变量
    private MessageReceiver messageReceiver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        main_list_bt = (ImageButton) view.findViewById(R.id.main_list_bt);
        main_play_pause_bt = (ImageButton) view.findViewById(R.id.main_play_pause_bt);
        main_like_bt = (ImageButton) view.findViewById(R.id.main_like_bt);
        main_recent_bt = (ImageButton) view.findViewById(R.id.main_recent_bt);
        main_count_tv = (TextView) view.findViewById(R.id.main_count_tv);
        main_search_bt = (ImageButton)view.findViewById(R.id.main_search_bt);

        main_count_tv.setText(String.valueOf(myApplication.getData().size()));

        if(myApplication.isPlay()){
            main_play_pause_bt.setImageResource(R.drawable.pausewhite);
        }



        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        IntentFilter intentFilter = new IntentFilter();
        messageReceiver = new MessageReceiver();

        intentFilter.addAction("CHANGEMAINBUTTON");
        intentFilter.addAction("com.example.MusicService.DETIAL");
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

        main_search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)(getActivity())).fragDown();
            }
        });


        main_play_pause_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentnotify = new Intent("notification_play_pause");
                getActivity().sendBroadcast(intentnotify);
            }
        });

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("CHANGEMAINBUTTON")) {
                if (myApplication.isPlay()) {
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                } else
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);
            }

            if(intent.getAction().equals("com.example.MusicService.DETIAL")){
                main_count_tv.setText(myApplication.getData().size()+"");
            }

        }
    }

}
