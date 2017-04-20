package com.example.MusicService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.Utils.ToastHelper;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务主要用于控制音乐播放(包括列表播放 按键播放 播放模式)
 * 控制前台服务
 */

public class MusicService extends Service {

    public static final int ORDER = 1;
    public static final int RANDOM = 2;
    public static final int LOOP = 3;

    private MyApplication myApplication;
    private MediaPlayer mediaPlayer = new MediaPlayer();    //创建媒体播放器
    private ArrayList<Map<String, String>> data = new ArrayList<>();
    private int play_mode = ORDER;      // o 顺序播放 r 随机播放 l 单曲循环
    private MBind mbind = new MBind();
    private int position;            //当前播放曲目的位置
    private RemoteViews contentView;
    private Notification notification;
    private PendingIntent pendingIntent;
    private NotificationCompat.Builder builder;
    private SQLiteDatabase db;

    MusicReceiver musicReceiver = new MusicReceiver();

    public class MusicReceiver extends BroadcastReceiver {

        //接收器
        @Override
        public void onReceive(Context context, Intent intent) {

            play_mode= myApplication.getPlay_mode();

            data = myApplication.getData(); //更新来自新下载的歌曲
            Log.e("tag", data.size()+"");
            if(data.size()!=0)
            if (intent.getAction().equals("com.example.MainActivity.STARTMUSIC")) {   //任何播放音乐的操作都要发送该广播

                if (intent.getBooleanExtra("NEXT", false)) {                                //判断按下的是否为下一首
                    contentView.setImageViewResource(R.id.play_image, R.drawable.ic_pause); //更改前台播放栏图标为暂停
                    notification = builder.setContent(contentView).build();
                    startForeground(1, notification);
                    if(position < data.size()-1) {
                        position = position + 1;  // 避免最后一首 选择下一首崩溃的情况
                    }
                    mediaPlayer.reset();        //下一首之前要重置播放器
                }

                if(intent.getBooleanExtra("PRE",false)){
                    contentView.setImageViewResource(R.id.play_image, R.drawable.ic_pause);
                    notification = builder.setContent(contentView).build();
                    startForeground(1, notification);                                       //判断是否为上一首 与下一首同理
                    if(position!=0) {
                        position = position - 1;
                        mediaPlayer.reset();
                    }else
                        ToastHelper.showToast("已经是第一首了");
                }

                if (intent.getBooleanExtra("POSITION", false)) {
                    setPosition(intent.getIntExtra("LOCATION", 0));
                        mediaPlayer.reset();                        //如果有位置信息 则根据发送过来的位置选择播放的曲目
                }

                initMediaPlayer(data.get(position).get("data"));        //根据当当前位置选择播放的曲目 初始化播放器并开始播放音乐
                mainMessageCallBack(); // 发送 歌曲数量 以及 当前歌曲
                upgradeDataNotification(); //notification 标题
                progressCallBack();
                putInData();
            }



            if (intent.getBooleanExtra("SEEK", false)) {
                mediaPlayer.seekTo(intent.getIntExtra("PROGRESS", 0));
            }

            if (intent.getAction().equals("notification_play_pause")) {
                if (mediaPlayer.isPlaying() && !intent.getBooleanExtra("LIST",false)) {
                    mediaPlayer.pause();
                    myApplication.setIsPlay(false);
                    contentView.setImageViewResource(R.id.play_image, R.drawable.ic_play);
                    notification = builder.setContent(contentView).build();
                      //设置前台服务图标
                } else {                                                            //Notification的点击事件 无法自己修改自己的图标、只能通过发广播
                    myApplication.setIsPlay(true);
                    Intent intentstartmusic = new Intent("com.example.MainActivity.STARTMUSIC");
                    if(!intent.getBooleanExtra("LIST",false)){
                        sendBroadcast(intentstartmusic);          //如果是列表中选择，则列表内启动服务播放。否则 继续播放
                    }
                }
                startForeground(1, notification);
                Intent intentchangeMain = new Intent("CHANGEMAINBUTTON"); //广播修改图标
                sendBroadcast(intentchangeMain);
            }

            if (intent.getAction().equals("CHANGENEXT")) {
                myApplication.setIsPlay(true);
                Intent intentplay = new Intent("com.example.MainActivity.STARTMUSIC"); //切歌对应广播
                intentplay.putExtra("NEXT", true);

                Intent intentchangeMain = new Intent("CHANGEMAINBUTTON");//广播修改图标
                sendBroadcast(intentchangeMain);
                sendBroadcast(intentplay);
            }

            if (intent.getAction().equals("com.example.MainActivity.REQUSETRES")) {   //请求更新主界面数据
                mainMessageCallBack();
            }

        }
    }


    @Override
    public void onCreate() {

        super.onCreate();
        try{
        myApplication = (MyApplication) getApplication();
        data = myApplication.getData();
        play_mode = myApplication.getPlay_mode();
        db = myApplication.getDp();
            position = myApplication.getPosition();

            registerMyReceiver();//注册广播

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) { //音乐播放完毕的监听器


                    mediaPlayer.reset(); //音乐停后不会reset,先reset 否则不能下一首

                    setPosition();  //播放完根据模式选择位置

                    Intent intent = new Intent("com.example.MusicService.PROGRESS"); //复位进度条
                    sendBroadcast(intent);

                    Intent intent2 = new Intent("com.example.MainActivity.STARTMUSIC"); //自动播放
                    sendBroadcast(intent2);
                }
            });
            initNotification();  //创建前台服务
        }catch (Exception e){
            e.printStackTrace();
            onDestroy();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mbind;
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(musicReceiver);

        super.onDestroy();
    }

    private void progressCallBack() {   //返回播放进度信息

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent("com.example.MusicService.PROGRESS");   //定时 1S 发送广播改变进度条以及歌词信息
                while (mediaPlayer.isPlaying()) {
                    if (!myApplication.isSeekBarTouch()) {
                        myApplication.setProgress(mediaPlayer.getCurrentPosition());
                        try {
                            Thread.sleep(1000);
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
        sendBroadcast(detialIntent);
        Intent intent = new Intent("CHANGEMAINBUTTON");
        myApplication.setSeekBarMax(Integer.parseInt(data.get(position).get("duration")));
        myApplication.setBottomTitle(data.get(position).get("title"));
        myApplication.setBottomSinger(data.get(position).get("singer"));
        sendBroadcast(intent);

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



    public void putInData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put("singer", data.get(position).get("singer"));
                values.put("duration", data.get(position).get("duration"));
                values.put("title", data.get(position).get("title"));
                values.put("data",data.get(position).get("data"));


                Log.e("data",data.get(position).get("title"));
                db.delete("Recent","title=?",new String[]{data.get(position).get("title")});
                db.insert("Recent", null, values);  //先删再加保证只存在一个

                SharedPreferences.Editor editor = getSharedPreferences("last", MODE_PRIVATE).edit();
                editor.putInt("position", position);
                editor.putString("singer", data.get(position).get("singer"));
                editor.putString("title", data.get(position).get("title"));
                editor.putString("duration",data.get(position).get("duration"));
                editor.apply();

            }
        }).start();  //播放历史 加入数据库存下



    }


    public int setPosition() {

        if (play_mode == ORDER) {
            if(position < data.size()-1) {
                position++;                                          // 避免最后一首 选择下一首崩溃的情况
            }
            mediaPlayer.reset();                                    //根据模式选择下一首播放歌曲的位置
        } else if (play_mode == RANDOM) {
            position = (int) (Math.rint(Math.random() * data.size()));
        }

        myApplication.setPosition(position);
        return position;
    }

    public int setPosition(int position) {
        this.position = position;                   //手动设置位置
        return position;
    }


    public class MBind extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    void registerMyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.MainActivity.STARTMUSIC");
        intentFilter.addAction("notification_play_pause");
        intentFilter.addAction("CHANGENEXT");
        intentFilter.addAction("com.example.MainActivity.REQUSETRES");      //注册广播接收器
        registerReceiver(musicReceiver, intentFilter);
    }

    void initNotification() {

        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.title_tv, data.get(position).get("title"));
        contentView.setTextViewText(R.id.singer_tv, data.get(position).get("singer"));
        contentView.setImageViewResource(R.id.next_image, R.drawable.ic_next);
        contentView.setImageViewResource(R.id.lyric_image, R.drawable.ic_lyricnotifi);
        contentView.setImageViewResource(R.id.head_image, R.drawable.ic_music);
        contentView.setImageViewResource(R.id.play_image, R.drawable.ic_play);  //前台服务设置按钮图标


        Intent intent = new Intent("notification_play_pause");
        contentView.setOnClickPendingIntent(R.id.play_image, PendingIntent.getBroadcast(MusicService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                                                                                //Button点击事件
        Intent intent2 = new Intent("CHANGENEXT");
        contentView.setOnClickPendingIntent(R.id.next_image, PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT));


        Intent intentstartactivity = new Intent(MusicService.this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(MusicService.this, 0, intentstartactivity, 0); //点击通知执行 打开活动

        builder = new NotificationCompat.Builder(MusicService.this);
        notification = builder
                .setContentIntent(pendingIntent)
                .setContent(contentView)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_yinfu)
                .build();
        startForeground(1, notification);  //启动前台服务


    }

    void upgradeDataNotification() { //修改前台服务 暂停与播放按钮
        contentView.setTextViewText(R.id.title_tv, data.get(position).get("title"));
        contentView.setTextViewText(R.id.singer_tv, data.get(position).get("singer"));

        if(mediaPlayer.isPlaying()){
            contentView.setImageViewResource(R.id.play_image,R.drawable.ic_pause);
        }else
            contentView.setImageViewResource(R.id.play_image,R.drawable.ic_play);

        notification = builder.setContent(contentView).build();
        startForeground(1, notification);
    }




}
