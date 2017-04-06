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
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MusicService extends Service {
    private File[] files;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<Map<String, String>> data = new ArrayList<>();
    private char play_mode = 'o';  // o 顺序播放 r 随机播放 l 单曲循环
    private boolean ispause = false;
    private boolean status = true; // 战士无用
    private MBind mbind = new MBind();
    public int position = 0; //当前播放曲目的位置
    public boolean isseekbartouch = false;
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


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getAbsolutePath().contains(data.get(position).get("title"))) {
                                    Intent intentlrc = new Intent("com.example.MusicService.LRC");
                                    intentlrc.putExtra("LRC",i);
                                    sendBroadcast(intentlrc);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();





            }

            progressCallBack();

            if (intent.getBooleanExtra("SEEK", false)) {
                mediaPlayer.seekTo(intent.getIntExtra("PROGRESS", 0));
            }

//                int size = data.get(position).get("fulltitle").indexOf('.');

//                    Log.e("loca",data.get(position).get("singer")+data.get(position).get("title"));


            if (intent.getAction().equals("com.example.LocalMusic.MODE")) {
                play_mode = intent.getCharExtra("MODE", 'o');
            }

            if (intent.getAction().equals("com.example.MainActivity.ISSEEKBARTOUCH")) {
                if (intent.getBooleanExtra("ISSEEKBARTOUCH", false))
                    isseekbartouch = true;
                else
                    isseekbartouch = false;
            }

            if (intent.getAction().equals("CHANGESELF")) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Log.e("info", "isplaying");
                    contentView.setImageViewResource(R.id.play_image, R.drawable.playdark);
                    notification = builder.setContent(contentView).build();
                    startForeground(1, notification);

                    Intent intentchangeMain = new Intent("com.example.MusicService.ISPLAY");
                    intentchangeMain.putExtra("ISPLAY", false);
                    sendBroadcast(intentchangeMain);

                } else {
                    contentView.setImageViewResource(R.id.play_image, R.drawable.pause);
                    notification = builder.setContent(contentView).build();
                    startForeground(1, notification);

                    Intent intentstartmusic = new Intent("com.example.MainActivity.STARTMUSIC");
                    sendBroadcast(intentstartmusic);

                    Intent intentchangeMain = new Intent("com.example.MusicService.ISPLAY");
                    intentchangeMain.putExtra("ISPLAY", true);
                    sendBroadcast(intentchangeMain);
                }
            }

            if (intent.getAction().equals("CHANGENEXT")) {
                Log.e("info", "CHANGENEXT");
                Intent intentchangeMain = new Intent("com.example.MusicService.ISPLAY");
                intentchangeMain.putExtra("ISPLAY", true);
                sendBroadcast(intentchangeMain);
                Intent intentplay = new Intent("com.example.MainActivity.STARTMUSIC");
                intentplay.putExtra("NEXT", true);
                sendBroadcast(intentplay);
                contentView.setImageViewResource(R.id.play_image, R.drawable.pause);
                notification = builder.setContent(contentView).build();
                startForeground(1, notification);
            }

            if (intent.getAction().equals("com.example.MusicService.NOTIFI")) {
                if (intent.getBooleanExtra("PLAY", false)) {
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

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "//Musiclrc");
        files = file.listFiles();


        Log.e("info", "SERVICE create");
        registerMyReceiver();//注册广播
        readMusicData(); //读取信息
        mainMessageCallBack();// 初始化界面信息

        Intent arraylistIntent = new Intent("com.example.MusicService.ARRAY");
        arraylistIntent.putExtra("ARRAY", data);
        sendBroadcast(arraylistIntent);            //将歌曲信息列表传给其他活动！


        try {
            SharedPreferences share = getSharedPreferences("data", MODE_PRIVATE);
            play_mode = share.getString("MODE", "orl").charAt(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) { //音乐播放完毕的监听器
                Log.e("info", "end of the music");

                mediaPlayer.reset(); //音乐停后不会reset

                setPosition();

                Intent intent = new Intent("com.example.MusicService.PROGRESS"); //复位进度条
                sendBroadcast(intent);

                Intent intent2 = new Intent("com.example.MainActivity.STARTMUSIC"); //激动播放
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
                    if (!isseekbartouch) {
                        intent.putExtra("PROGRESS", mediaPlayer.getCurrentPosition());
                        sendBroadcast(intent);
                        Log.e("info", "callback");
                        try {
                            Thread.sleep(400);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    private void mainMessageCallBack() {  //返回 歌曲数量 以及 当前歌曲
        Intent detialIntent = new Intent("com.example.MusicService.DETIAL");
        detialIntent.putExtra("MAXPROGRESS", Integer.parseInt(data.get(position).get("duration")));
        Log.e("info", Integer.parseInt(data.get(position).get("duration")) + "");
        detialIntent.putExtra("TITLE", data.get(position).get("title"));
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

    private void readMusicData() {

        String[] want = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION};

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, want, MediaStore.Audio.Media.DURATION + ">60000", null, MediaStore.Audio.Media.TITLE);
        if (cursor != null && cursor.moveToFirst())
            do {
                Map<String, String> map = new HashMap<>();
                map.put("title", cursor.getString(0));
                map.put("data", cursor.getString(1));           //读取音乐文件
                map.put("singer", cursor.getString(2));
                map.put("fulltitle", cursor.getString(3));
                map.put("duration", cursor.getInt(4) + "");

                data.add(map);

            } while (cursor.moveToNext());

        for (int i = 0; i < data.size() - 1; i++) {
            if (data.get(i).get("title").equals(data.get(i + 1).get("title"))) {
                data.remove(data.get(i));
            }
        }

        if (cursor != null)
            cursor.close();
    }


    public int setPosition() {

        if (play_mode == 'o') {
            position++;
            mediaPlayer.reset();          //根据模式选择下一首播放歌曲的位置
        } else if (play_mode == 'r') {
            position = (int) (Math.rint(Math.random() * data.size()));
        }

        return position;
    }

    public int setPosition(int position) {
        this.position = position;
        return position;
    }

    public void pauseMusic() {
        ispause = true;

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
        intentFilter.addAction("com.example.MainActivity.ISSEEKBARTOUCH");
        intentFilter.addAction("CHANGESELF");
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

        Intent intent = new Intent("CHANGESELF");
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
