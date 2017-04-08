package com.example.mylatouttest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Base64;


import com.example.MusicService.MusicService;
import com.example.VolumechangeReceiver.VolumnChangeReceiver;
import com.example.local_music.LocalMusic;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MyApplication myApplication;
    static TextView lrc;
    int max;
    boolean ispause = true; //判断播放状态
    SeekBar seekbar;
    ImageButton main_list_bt;
    ImageButton main_play_pause_bt;
    ImageButton main_like_bt;
    ImageButton main_recent_bt;
    ImageButton main_next_bt;
    ImageButton STOP;
    LyricInfo lyricInfo;
    TextView main_fulltitle_tv;
    TextView main_count_tv;
    public static boolean stopThread = false;
    Thread lyricThread = new Thread();

    String temptitle = "";
    int progress;
    File[] files;
    ArrayList<Map<String, String>> data = null;
    long time;
    int key = 0;

    private VolumnChangeReceiver volumnChangeReceiver;
    private MessageReceiver messageReceiver;
    private MusicService musicService;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lrc.setText(msg.obj.toString());
        }
    };


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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplayer_main);


        Intent intent = new Intent("com.example.MainActivity.REQUSETRES");
        sendBroadcast(intent);

        lyricInfo = new LyricInfo();
        lyricInfo.lineinfo = new ArrayList<>();


        myApplication = (MyApplication) getApplication();
        myApplication.setSeekBar(seekbar);
        myApplication.setMain_play_pause_bt(main_play_pause_bt);

        readytoplay();

//        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottomplayer,null);
//        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.musicplayer_main,null) ;
//        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.showAtLocation(rootView,Gravity.BOTTOM,0,0);


        this.data = myApplication.getData();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("info", "asd");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {     //按下进度条 先调用onStartTrackingTouch一次，再调用onProgressChanged一次
                seekBar.setMax(max);
                if (ispause) {
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                    ispause = false;
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


        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=%E6%B5%AE%E5%A4%B8&duration=218392&hash=")
                        .build();

                try {


                    Response response = client.newCall(request).execute();
                    String responsedata = response.body().string();
                    Log.e("eqqw",responsedata);
                    Gson gson =new Gson();
                    LyricMessageTaker lyricMessageTaker = gson.fromJson(responsedata,LyricMessageTaker.class);

                    byte[] lyric = new byte[1];
//                    byte[] lyric = Base64.decode(lyricMessageTaker.getCandidates(),Base64.DEFAULT);
                    Log.e("eqqw",lyricMessageTaker.getCandidates().get(1).singer);
                    String directory = Environment.getExternalStorageDirectory().getAbsoluteFile().getPath()+"/MyLyric/";
                    Log.e("eqqw",directory);
                    File file = new File(directory);

                    if(file.exists())
                    Log.e("eee","ok");
                    else
                    file.mkdir();

                    file = new File(directory+"simple3.lrc");
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(lyric);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();




    }

    @Override
    protected void onDestroy() {
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
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("arrayList", (ArrayList) mapArrayList);
//                acitvityintent.putExtras(bundle);
                startActivity(acitvityintent);
                break;

            case R.id.main_play_pause_bt:
                Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                Intent intentnotify1 = new Intent("com.example.MusicService.NOTIFI");
                if (ispause) {
                    intent.putExtra("ISPAUSE", true);
                    sendBroadcast(intent);
                    intentnotify1.putExtra("PLAY", true);
                    sendBroadcast(intentnotify1);
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                    ispause = false;

                    myApplication.setIsPlay(true);

                } else {
                    musicService.pauseMusic();
                    sendBroadcast(intentnotify1);
                    ispause = true;
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);

                    myApplication.setIsPlay(false);
                }


                break;

            case R.id.main_like_bt:
                Toast.makeText(this, "like", Toast.LENGTH_SHORT).show();

                break;

            case R.id.main_recent_bt:
                Toast.makeText(this, "recent", Toast.LENGTH_SHORT).show();
                break;

            case R.id.NEXT:
                myApplication.setIsPlay(true);
                Log.e("info", "next");
                stopThread = true;
                stopThread = false;
                seekbar.setProgress(0);
                Intent intentnext = new Intent("com.example.MainActivity.STARTMUSIC");
                intentnext.putExtra("NEXT", true);
                sendBroadcast(intentnext);

                Intent intentnotify = new Intent("com.example.MusicService.NOTIFI");
                intentnotify.putExtra("PLAY", true);
                sendBroadcast(intentnotify);
                main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                ispause = false;
                lyricThread.interrupt();
                break;

            case R.id.STOP:
                Log.e("reset", "reset");
                musicService.resetMusic();
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
        intentFilter.addAction("com.example.MusicService.ARRAY");
        intentFilter.addAction("com.example.LocalMusic.PLAY");
        intentFilter.addAction("com.example.MusicService.ISPLAY");
        intentFilter.addAction("com.example.MusicService.ISPAUSE");
        intentFilter.addAction("com.example.MusicService.LRC");
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

    private boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }//运行权限


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("permit", "permit");
            return true;
        }
        Log.e("permit", "didnt permit");
        return false;
    }

    private void insertDesign() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;            //隐藏导航栏
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

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
        main_next_bt = (ImageButton) findViewById(R.id.NEXT);
        main_fulltitle_tv = (TextView) findViewById(R.id.tv);
        main_count_tv = (TextView) findViewById(R.id.main_count_tv);
        lrc = (TextView) findViewById(R.id.lrc);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        STOP = (ImageButton) findViewById(R.id.STOP);

        STOP.setOnClickListener(this);
        main_like_bt.setOnClickListener(this);
        main_recent_bt.setOnClickListener(this);
        main_list_bt.setOnClickListener(this);
        main_play_pause_bt.setOnClickListener(this);
        main_next_bt.setOnClickListener(this);

    }


    private void readytoplay() {

        while (!checkPermission()) {
            sleep(500);
        }


        File file = new File(Environment.getExternalStorageDirectory().getPath() + "//Musiclrc");
        files = file.listFiles();

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        startService(intent);

        getView();
        insertDesign();
        registerMyReceiver();

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

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.example.MusicService.PROGRESS")) {
                progress = intent.getIntExtra("PROGRESS", 0);
                seekbar.setProgress(progress);

                lyricThread = new Thread(new Runnable() { //处理歌词的线程

                    @Override
                    public void run() {

                        try {
                            int temp = 0;
                            while (temp < lyricInfo.lineinfo.size() - 1) {
                                Log.e("inter", "inter");
                                Message message = new Message();
                                message.obj = "";

                                long start = System.currentTimeMillis();

                                while ((System.currentTimeMillis() - start) < 400) {
                                    if (stopThread) return;
                                }
                                for (int j = 0; j < lyricInfo.lineinfo.size() - 1; j++) {

                                    if (lyricThread.isInterrupted()) break;

                                    if (progress >= lyricInfo.lineinfo.get(j).start && progress <= lyricInfo.lineinfo.get(j + 1).start) {
                                        message.obj = lyricInfo.lineinfo.get(j).line;
                                        temp = j;
                                        break;
                                    }


                                }
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            Log.e("inter", "catch");
                        }
                    }
                });


                try {

                    if (!temptitle.equals(main_fulltitle_tv.getText().toString())) {
                        temptitle = main_fulltitle_tv.getText().toString();
                        Log.e("fa", temptitle);
                        int pos = 0;
                        for (; pos < files.length; pos++) {
                            if (files[pos].getAbsolutePath().contains(temptitle)) {
                                Log.e("found", "found");
                                getLRC(files[pos], lyricInfo);   //找到并导入对应歌词到类中
                                lyricThread.start();
                                break;
                            }
                            if (pos == files.length - 1)
                                lrc.setText("没找到歌词");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } // 修改进度

            if (intent.getAction().equals("com.example.MusicService.DETIAL")) {
                main_fulltitle_tv.setText(intent.getStringExtra("TITLE"));
                main_count_tv.setText(intent.getIntExtra("COUNT", 0) + "");
                max = intent.getIntExtra("MAXPROGRESS", 0);
                seekbar.setMax(intent.getIntExtra("MAXPROGRESS", 0));

            } //接受并初始化/修改 当前歌曲 以及歌曲数目 歌词


//            if (intent.getAction().equals("com.example.MusicService.ARRAY")) {
//                mapArrayList = (ArrayList<Map<String, String>>) intent.getSerializableExtra("ARRAY");
//            }   // 中转继续传给其他活动

            if (intent.getAction().equals("com.example.LocalMusic.PLAY")) {
                ispause = false;
                main_play_pause_bt.setImageResource(R.drawable.pausewhite);
            }

            if (intent.getAction().equals("com.example.MusicService.ISPLAY")) {

                if (intent.getBooleanExtra("ISPLAY", false))
                    main_play_pause_bt.setImageResource(R.drawable.pausewhite);
                else
                    main_play_pause_bt.setImageResource(R.drawable.startwhite);
            }

        }
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
