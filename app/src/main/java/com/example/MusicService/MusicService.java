package com.example.MusicService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MusicService extends Service {

    private MyApplication myApplication;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<Map<String, String>> data = new ArrayList<>();
    private char play_mode = 'o';  // o 顺序播放 r 随机播放 l 单曲循环
    private MBind mbind = new MBind();
    public int position = 0; //当前播放曲目的位置
    private RemoteViews contentView;
    private Notification notification;
    private PendingIntent pendingIntent;
    private NotificationCompat.Builder builder;

    MusicReceiver musicReceiver = new MusicReceiver();

    public class MusicReceiver extends BroadcastReceiver {

        //接收器
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.example.MainActivity.STARTMUSIC")) {

                Log.e("info", "get");

                if (intent.getBooleanExtra("NEXT", false)) {
                    Log.e("info", "getnext");
                    setPosition();
                    mediaPlayer.reset();
                }

                if (intent.getBooleanExtra("POSITION", false)) {
                    setPosition(intent.getIntExtra("LOCATION", 0));
                    mediaPlayer.reset(); //同样的 不reset就变成继续了
                }

                upgradeDataNotification();

                mainMessageCallBack();

                initMediaPlayer(data.get(position).get("data"));

            }

            progressCallBack();


            if (intent.getBooleanExtra("SEEK", false)) {
                mediaPlayer.seekTo(intent.getIntExtra("PROGRESS", 0));
            }

            if (intent.getAction().equals("com.example.LocalMusic.MODE")) {
                play_mode = intent.getCharExtra("MODE", 'o');
            }

            if (intent.getAction().equals("notification_play_pause")) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    myApplication.setIsPlay(false);

                    Intent intentchangeMain = new Intent("CHANGEMAINBUTTON");
                    sendBroadcast(intentchangeMain);

                } else {

                    myApplication.setIsPlay(true);

                    Intent intentstartmusic = new Intent("com.example.MainActivity.STARTMUSIC");
                    sendBroadcast(intentstartmusic); //继续播放

                    Intent intentchangeMain = new Intent("CHANGEMAINBUTTON");
                    sendBroadcast(intentchangeMain);

                }

                Intent intentnoti = new Intent("com.example.MusicService.NOTIFI");
                context.sendBroadcast(intentnoti);
            }

            if (intent.getAction().equals("CHANGENEXT")) {

                Intent intentplay = new Intent("com.example.MainActivity.STARTMUSIC");
                intentplay.putExtra("NEXT", true);
                sendBroadcast(intentplay);

                myApplication.setIsPlay(true);
                Intent intentchangeMain = new Intent("CHANGEMAINBUTTON");
                sendBroadcast(intentchangeMain);

                contentView.setImageViewResource(R.id.play_image, R.drawable.pause);
                notification = builder.setContent(contentView).build();
                startForeground(1, notification);
            }

            if (intent.getAction().equals("com.example.MusicService.NOTIFI")) {
                Log.e("get", "get" + myApplication.isPlay());
                if (myApplication.isPlay()) {
                    contentView.setImageViewResource(R.id.play_image, R.drawable.pause);
                    notification = builder.setContent(contentView).build();
                    startForeground(1, notification);
                } else {
                    contentView.setImageViewResource(R.id.play_image, R.drawable.playdark);
                    notification = builder.setContent(contentView).build();
                    startForeground(1, notification);
                }
            }

            if (intent.getAction().equals("com.example.MainActivity.REQUSETRES")) {
                mainMessageCallBack();
            }


        }
    }


    @Override
    public void onCreate() {

        super.onCreate();

        myApplication = (MyApplication) getApplication();
        data = myApplication.getData();

        Log.e("info", "SERVICE create");
        registerMyReceiver();//注册广播
        mainMessageCallBack();// 初始化界面信息

        Intent arraylistIntent = new Intent("com.example.MusicService.ARRAY");
        arraylistIntent.putExtra("ARRAY", data);
        sendBroadcast(arraylistIntent);            //将歌曲信息列表传给其他活动！


        try {
            SharedPreferences share = getSharedPreferences("data", MODE_PRIVATE);
            play_mode = share.getString("MODE", "orl").charAt(2);
        } catch (Exception e) {             //根据上次退出的选择模式
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) { //音乐播放完毕的监听器
                Log.e("info", "end of the music");

                mediaPlayer.reset(); //音乐停后不会reset,先reset 否则不能下一首

                setPosition();  //播放完根据模式选择位置

                Intent intent = new Intent("com.example.MusicService.PROGRESS"); //复位进度条
                sendBroadcast(intent);

                Intent intent2 = new Intent("com.example.MainActivity.STARTMUSIC"); //自动播放
                sendBroadcast(intent2);
            }
        });


        initNotification();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("info", "service bind");
        return mbind;
    }

    @Override
    public void onDestroy() {
        Log.e("info", "service destory");
        unregisterReceiver(musicReceiver);
        super.onDestroy();
    }

    private void progressCallBack() {   //返回播放进度信息

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent("com.example.MusicService.PROGRESS");
                while (mediaPlayer.isPlaying()) {
                    if (!myApplication.isSeekBarTouch()) {
                        myApplication.setProgress(mediaPlayer.getCurrentPosition());
                        try {
                            Thread.sleep(400);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendBroadcast(intent);
                    }
                }
            }
        }).start();

    }

    private void mainMessageCallBack() {  //返回 歌曲数量 以及 当前歌曲
        Intent detialIntent = new Intent("com.example.MusicService.DETIAL");
        myApplication.setSeekBarMax(Integer.parseInt(data.get(position).get("duration")));
        myApplication.setBottomTitle(data.get(position).get("title"));
        myApplication.setBottomSinger(data.get(position).get("singer"));

        Log.e("info", Integer.parseInt(data.get(position).get("duration")) + "");
        detialIntent.putExtra("COUNT", data.size());
        sendBroadcast(detialIntent);
    }

    private void initMediaPlayer(String location) {
        try {
            File file = new File(location);

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();              //初始化播放器 并播放
            }
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
    }


    public int setPosition() {

        if (play_mode == 'o') {
            position++;
            mediaPlayer.reset();          //根据模式选择下一首播放歌曲的位置
        } else if (play_mode == 'r') {
            position = (int) (Math.rint(Math.random() * data.size()));
        }

        myApplication.setPosition(position);
        return position;
    }

    public int setPosition(int position) {
        this.position = position;
        return position;
    }

    public void pauseMusic() {
        myApplication.setIsPlay(false);
        if (mediaPlayer.isPlaying()) {       //停止音乐
            mediaPlayer.pause();
        }
    }

    public void resetMusic() {
        mediaPlayer.reset();
    }


    public class MBind extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    void registerMyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.MainActivity.STARTMUSIC");
        intentFilter.addAction("com.example.MainActivity.NEXTMUSIC");
        intentFilter.addAction("com.example.LocalMusic.MODE");
        intentFilter.addAction("notification_play_pause");
        intentFilter.addAction("CHANGENEXT");
        intentFilter.addAction("com.example.MusicService.NOTIFI");
        intentFilter.addAction("com.example.MainActivity.REQUSETRES");
        registerReceiver(musicReceiver, intentFilter);
    }

    void initNotification() {

        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.title_tv, data.get(position).get("title"));
        contentView.setTextViewText(R.id.singer_tv, data.get(position).get("singer"));
        contentView.setImageViewResource(R.id.next_image, R.drawable.nextdark);
        contentView.setImageViewResource(R.id.lyric_image, R.drawable.lyric);
        contentView.setImageViewResource(R.id.head_image, R.drawable.music);
        contentView.setImageViewResource(R.id.play_image, R.drawable.playdark);

        Intent intent = new Intent("notification_play_pause");
        PendingIntent changependingIntent = PendingIntent.getBroadcast(MusicService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); //点击事件
        contentView.setOnClickPendingIntent(R.id.play_image, changependingIntent);

        Intent intent2 = new Intent("CHANGENEXT");
        contentView.setOnClickPendingIntent(R.id.next_image, PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT));


        Intent intentstartactivity = new Intent(MusicService.this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(MusicService.this, 0, intentstartactivity, 0);
        builder = new NotificationCompat.Builder(MusicService.this);
        notification = builder
                .setContentIntent(pendingIntent)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.delete)
                .build();
        startForeground(1, notification);
    }

    void upgradeDataNotification() {
        contentView.setTextViewText(R.id.title_tv, data.get(position).get("title"));
        contentView.setTextViewText(R.id.singer_tv, data.get(position).get("singer"));
        notification = builder.setContent(contentView).build();
        startForeground(1, notification);
    }


}
