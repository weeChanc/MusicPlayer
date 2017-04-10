package com.example.mylatouttest;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Base64;


import com.example.MusicService.MusicService;
import com.example.VolumechangeReceiver.VolumnChangeReceiver;
import com.example.fragment.FragMain;
import com.example.fragment.LikeListFrag;
import com.example.local_music.LocalMusic;
import com.example.mylatouttest.Lyric.LyricJson;
import com.example.mylatouttest.Lyric.LyricMessageTaker;
import com.example.song.SongGetter;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    static TextView lrc; //歌词的textview
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lrc.setText(msg.obj.toString());   //处理歌词的Handler
        }
    };

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
    SeekBar bottomSeekbar;
    View bottomPlayer;
    View destopLyric;
    FragmentManager fm = getSupportFragmentManager();

    ArrayList<Map<String, String>> data = null; //所有歌曲信息
    LyricInfo lyricInfo; //当前播放的歌曲信息
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myApplication = (MyApplication) getApplication();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplayer_main);

        Intent intent = new Intent("com.example.MainActivity.REQUSETRES");
        sendBroadcast(intent);

        lyricInfo = new LyricInfo();
        lyricInfo.lineinfo = new ArrayList<>();

        file = myApplication.getFile();
        files = file.listFiles();

        readytoplay();
        initWindows();

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SongGetter.download(SongGetter.getAllSong(editText.getText().toString()).get(0).getFileHash());
                        Toast.makeText(MainActivity.this, "downloade succeed!", Toast.LENGTH_SHORT).show();
                    }
                }).start();

            }
        });

        

    }


    @Override
    protected void onDestroy() {
        manager.removeView(bottomPlayer);
        Log.e("info", "MainAcitivit Destory");
        unregisterReceiver(volumnChangeReceiver);
        unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_list_bt:
                Intent acitvityintent = new Intent(this, LocalMusic.class);
                startActivity(acitvityintent);
                break;

            case R.id.main_play_pause_bt:
                Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                Intent intentnotify1 = new Intent("com.example.MusicService.NOTIFI");
                if (myApplication.isPlay()) {
                    myApplication.setIsPlay(false);
                    musicService.pauseMusic();
                    sendBroadcast(intentnotify1);
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);

                } else {
                    myApplication.setIsPlay(true);
                    intent.putExtra("ISPAUSE", true);
                    sendBroadcast(intent);
                    sendBroadcast(intentnotify1);
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);

                }

                break;

            case R.id.main_like_bt:
                Toast.makeText(this, "like", Toast.LENGTH_SHORT).show();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                LikeListFrag likeListFrag = new LikeListFrag();
                ft.add(R.id.frag_container, likeListFrag);
                ft.commit();

                break;

            case R.id.main_recent_bt:
                ft = fm.beginTransaction();
                FragMain fragMain = new FragMain();
                ft.add(R.id.frag_container, fragMain);
                ft.commit();
                break;



        }
    }

    private void registerMyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION"); //音量变化广播
        intentFilter.addAction("android.intent.action.HEADSET_PLUG"); //耳机插拔广播
        volumnChangeReceiver = new VolumnChangeReceiver();
        registerReceiver(volumnChangeReceiver, intentFilter); //注册广播

        messageReceiver = new MessageReceiver();

        intentFilter.addAction("com.example.MusicService.PROGRESS");
        intentFilter.addAction("com.example.MusicService.DETIAL");
        intentFilter.addAction("com.example.LocalMusic.PLAY");
        intentFilter.addAction("CHANGEMAINBUTTON");
        registerReceiver(messageReceiver, intentFilter);

    }

    @Override
    public void onBackPressed() {
        if (key++ == 0) {
            time = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time > 2000) {
            key = 0;
        } else finish();


    }

    private void insertDesign() {

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);                //隐藏状态栏
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void getView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar;
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        main_list_bt = (ImageButton) findViewById(R.id.main_list_bt);
        main_play_pause_bt = (ImageButton) findViewById(R.id.main_play_pause_bt);
        main_like_bt = (ImageButton) findViewById(R.id.main_like_bt);
        main_recent_bt = (ImageButton) findViewById(R.id.main_recent_bt);
        main_count_tv = (TextView) findViewById(R.id.main_count_tv);
        lrc = (TextView) findViewById(R.id.lrc);

        main_like_bt.setOnClickListener(this);
        main_recent_bt.setOnClickListener(this);
        main_list_bt.setOnClickListener(this);
        main_play_pause_bt.setOnClickListener(this);


    }


    private void readytoplay() {

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        startService(intent);

        getView();
        insertDesign();
        registerMyReceiver();

        setThread();
        myApplication.setThread(lyricThread);



    }

    void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            Log.e("catch", "catch");
            e.printStackTrace();
        }
    }

    void getLRC(File file, LyricInfo lyricinfo) {
        try {
            FileInputStream fip = new FileInputStream(file);
            InputStreamReader ips = new InputStreamReader(fip);
            BufferedReader bufferedReader = new BufferedReader(ips);

            lyricinfo.lineinfo = new ArrayList<>();

            String Line;

            while ((Line = bufferedReader.readLine()) != null) {

                int last = Line.indexOf(']');

                if (Line.startsWith("[ar:")) {
                    lyricinfo.artist = Line.substring(4, last);

                }

                if (Line.startsWith("[ti:")) {
                    lyricinfo.title = Line.substring(4, last);
                }

                if (Line.startsWith("[0")) {

                    LineInfo currentlineinfo = new LineInfo();

                    currentlineinfo.line = Line.substring(last + 1).trim();
                    currentlineinfo.start = (int) (Integer.parseInt(Line.substring(1, 3).trim()) * 60 * 1000 + Double.parseDouble(Line.substring(4, last).trim()) * 1000);
                    lyricinfo.lineinfo.add(currentlineinfo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean seekLyric(){
        files = file.listFiles();
        int pos = 0;
        Log.e("tag",temptitle);
        for (; pos <= files.length; pos++) {
            if (files.length > 0 && files[pos].getAbsolutePath().contains(temptitle)) {
                Log.e("tag", "找到了歌词");
                getLRC(files[pos], lyricInfo);   //找到并导入对应歌词到类中
                lyricThread.start();
                return true;  //找到返回true
            }
            if(pos == files.length-1)
                return false;
        }

        return false;
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.example.MusicService.PROGRESS")) {
                bottomSeekbar.setProgress(myApplication.getProgress());


                try {

                    if (!temptitle.equals(bottomtitle.getText().toString())) {
                        temptitle =bottomtitle.getText().toString();

                        if (!seekLyric()) {
                            data = myApplication.getData();
                            Log.e("tag","找不到歌词，准备搜索");
                            lrc.setText("成哥为你搜索歌词中");

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        Request request = new Request.Builder().url(data.get(myApplication.getPosition()).get("URL")).build();
                                        Response response = okHttpClient.newCall(request).execute();
                                        Gson gson = new Gson();
                                        LyricMessageTaker lyricMessageTaker = gson.fromJson(response.body().string(), LyricMessageTaker.class);

                                        int lyricmount = lyricMessageTaker.getCandidates().size();
                                        for (int i = 0; i < lyricmount  ; i++) {
                                            if (lyricMessageTaker.getCandidates().get(i).getSinger().equals(data.get(myApplication.getPosition()).get("singer"))) {

                                                request = new Request.Builder().url(lyricMessageTaker.getCandidates().get(i).initURL()).build();
                                                response = okHttpClient.newCall(request).execute();
                                                LyricJson lyricJson = gson.fromJson(response.body().string(), LyricJson.class);
                                                byte[] lyric = Base64.decode(lyricJson.getContent(), Base64.DEFAULT);
                                                File file = new File(Environment.getExternalStorageDirectory().getPath() + "//MyLyric//" + data.get(myApplication.getPosition()).get("title") + ".lrc");
                                                if (!file.exists()) {
                                                    Log.e("tag", "网络上找到了歌词，写入中");
                                                    file.createNewFile();
                                                    FileOutputStream fos = new FileOutputStream(file);
                                                    fos.write(lyric);
                                                    Log.e("tag", "写入成功");
                                                    fos.close();
                                                    seekLyric();
                                                }
                                                break;
                                            }
                                            if( i == lyricmount - 1) {
                                                Log.e("tag","找不到歌词");
                                                lrc.setText("连成哥都不能帮你找到歌词了");
                                            }
                                        }
                                        if(lyricmount == 0 ) {
                                            Log.e("tag","找不到歌词");
                                            lrc.setText("对不起,找不到歌词");
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }





            } // 修改进度de guangbo

            if (intent.getAction().equals("com.example.MusicService.DETIAL")) {
                bottomsinger.setText(myApplication.getBottomSinger());
                bottomtitle.setText(myApplication.getBottomTitle());
                main_count_tv.setText(intent.getIntExtra("COUNT", 0) + "");
                max = myApplication.getSeekBarMax();
                bottomSeekbar.setMax(max);

            } //接受并初始化/修改 当前歌曲 以及歌曲数目 歌词

            if (intent.getAction().equals("com.example.LocalMusic.PLAY")) {
                myApplication.setIsPlay(true);
                main_play_pause_bt.setImageResource(R.drawable.pausewhite);
            }

            if(intent.getAction().equals("CHANGEMAINBUTTON")){
                if(myApplication.isPlay()){
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                }
                else
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);
            }

        }
    }

    private void initWindows(){
        manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Display display = manager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        layoutParams.gravity = Gravity.LEFT| Gravity.BOTTOM;
        layoutParams.x= 0;
        layoutParams.y= 0;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = displayMetrics.heightPixels / 9;
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        bottomPlayer = View.inflate(getApplicationContext(),R.layout.bottomplayer,null);
        bottomtitle = (TextView)bottomPlayer.findViewById(R.id.bottom_title);
        bottomhead = (ImageView) bottomPlayer.findViewById(R.id.bottom_head);
        bottomnext = (ImageButton) bottomPlayer.findViewById(R.id.bottom_next);
        bottomsinger = (TextView) bottomPlayer.findViewById(R.id.bottomsinger);
        bottomSeekbar = (SeekBar)bottomPlayer.findViewById(R.id.bottom_seekbar);
        bottomnext.setImageResource(R.drawable.nextbule);
        bottomtitle.setText("标题");
        bottomhead.setImageResource(R.drawable.add);

        manager.addView(bottomPlayer,layoutParams);

        bottomnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lyricThread.interrupt();

                myApplication.setIsPlay(true);
                Log.e("info", "next");

                bottomSeekbar.setProgress(0);
                Intent intentnext = new Intent("com.example.MainActivity.STARTMUSIC");
                intentnext.putExtra("NEXT", true);
                sendBroadcast(intentnext);
                main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                Intent intentnotify = new Intent("com.example.MusicService.NOTIFI");
                sendBroadcast(intentnotify);




            }
        });

        bottomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myApplication.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {     //按下进度条 先调用onStartTrackingTouch一次，再调用onProgressChanged一次
                seekBar.setMax(max);
                if (!myApplication.isPlay()) {
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                    myApplication.setIsPlay(true);
                }
                myApplication.setIsSeekBarTouch(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent2 = new Intent("com.example.MainActivity.STARTMUSIC");
                intent2.putExtra("PROGRESS", seekBar.getProgress() - 1);
                intent2.putExtra("SEEK", true);
                sendBroadcast(intent2);
                myApplication.setIsSeekBarTouch(false);

            }
        });
    }

    private void setThread(){
        lyricThread = new Thread(new Runnable() { //处理歌词的线程

            @Override
            public void run() {
                try {
                    int temp = 0;
                    while (temp < lyricInfo.lineinfo.size() - 1) {
                        Log.e("tag", "处理歌词线程");
                        Message message = new Message();
                        message.obj = "";

                        long start = System.currentTimeMillis();

                        Thread.sleep(400);

                        for (int j = 0; j < lyricInfo.lineinfo.size() - 1; j++) {

                            if (myApplication.getProgress() >= lyricInfo.lineinfo.get(j).start && myApplication.getProgress() <= lyricInfo.lineinfo.get(j + 1).start) {
                                message.obj = lyricInfo.lineinfo.get(j).line;
                                temp = j;
                                break;
                            }

                        }
                        handler.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("tag","线程被打断了");
                    return ;
                }
            }
        });
    }

    private class LyricInfo {
        List<LineInfo> lineinfo;
        String artist;
        String title;
    }

    private class LineInfo {

        int start;
        String line;
    }


}
