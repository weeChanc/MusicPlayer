package com.example.MusicService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Exchanger;

/**
 * Created by 铖哥 on 2017/3/30.
 */

public class MusicService extends Service {

    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<Map<String, String>> data = new ArrayList<>();
    private char play_mode = 'o';  // o 顺序播放 r 随机播放 l 单曲循环
    private boolean ispause = false;
    private boolean status = true; // 战士无用
    private MBind mbind = new MBind();
    public int position = 0; //当前播放曲目的位置
    public boolean isseekbartouch = false;


    MusicReceiver musicReceiver = new MusicReceiver();

    public class MusicReceiver extends BroadcastReceiver {


        //接收器
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.example.MainActivity.STARTMUSIC")) {

                Log.e("info", "get");

                if (intent.getBooleanExtra("NEXT", false)) {
                    setPosition();
                    mediaPlayer.reset();
                }

                if (intent.getBooleanExtra("POSITION", false)) {
                    setPosition(intent.getIntExtra("LOCATION", 0));
                    mediaPlayer.reset(); //同样的 不reset就变成继续了
                }

                mainMessageCallBack();

                initMediaPlayer(data.get(position).get("data"));

                    progressCallBack();

                if (intent.getBooleanExtra("SEEK", false)) {
                    mediaPlayer.seekTo(intent.getIntExtra("PROGRESS", 0));
                }
            }

            if (intent.getAction().equals("com.example.LocalMusic.MODE")) {
                play_mode = intent.getCharExtra("MODE",'o');
            }

            if(intent.getAction().equals("com.example.MainActivity.ISSEEKBARTOUCH")){
                if (intent.getBooleanExtra("ISSEEKBARTOUCH", false))
                    isseekbartouch = true;
                else
                    isseekbartouch = false;
            }

        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("info","create");
        registerMyReceiver();//注册广播
        readMusicData(); //读取信息
        mainMessageCallBack();// 初始化界面信息

        Intent arraylistIntent = new Intent("com.example.MusicService.ARRAY");
        arraylistIntent.putExtra("ARRAY", data);
        sendBroadcast(arraylistIntent);            //将歌曲信息列表传给其他活动！

        try {
            SharedPreferences share = getSharedPreferences("data", MODE_PRIVATE);
            play_mode = share.getString("MODE","orl").charAt(2);
        }catch (Exception e){
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


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("info", "bind");
        return mbind;
    }

    @Override
    public void onDestroy() {
        Log.e("info", "destory");
        unregisterReceiver(musicReceiver);
        super.onDestroy();
    }

    private void progressCallBack() {   //返回播放进度信息

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent("com.example.MusicService.PROGRESS");
                    while (mediaPlayer.isPlaying()) {
                        if(!isseekbartouch) {
                            intent.putExtra("PROGRESS", mediaPlayer.getCurrentPosition());
                            sendBroadcast(intent);
                            try {
                                Thread.sleep(1000);
                            }catch(Exception e){
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
        Log.e("info",Integer.parseInt(data.get(position).get("duration"))+"");
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
                MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DURATION};

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,  want ,  null, null, MediaStore.Audio.Media.TITLE);
        if (cursor != null && cursor.moveToFirst())
            do {
                Map<String, String> map = new HashMap<>();
                map.put("title", cursor.getString(0));
                map.put("data", cursor.getString(1));           //读取音乐文件
                map.put("singer", cursor.getString(2));
                map.put("fulltitle", cursor.getString(3));
                map.put("duration",cursor.getInt(4)+"");

                data.add(map);

            } while (cursor.moveToNext());

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
        registerReceiver(musicReceiver, intentFilter);
    }

    public ArrayList<Map<String, String>> getdata() {
        return data;
    }


}
