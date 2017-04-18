package com.example.MyAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.example.song.Hash;
import com.example.song.SongData;
import com.example.song.SongDataGetter;
import com.example.song.SongGetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 铖哥 on 2017/4/4.
 */

/**
 * 该适配器为底部播放窗口 以及 歌词 两个界面的适配器
 * 用于获取歌词 显示歌词 修改/实时更新seekbar进度条 标题栏 以及控制歌曲的暂停 播放 下一首 上一首
 */



public class ViewPagerAdapter extends PagerAdapter {

    private List<View> list;
    private ArrayList<Map<String, String>> data = null; //所有歌曲信息
    private ImageButton bottomnext;
    private ImageButton bottomprivious;
    private ImageButton bottomplay_pause;
    private TextView bottomtitle;
    private TextView bottomsinger;
    private SeekBar bottomSeekbar;
    private SongData songData;

    Bitmap bitmap;


    private static TextView lyric1;
    private static TextView lyric2;

    private MyApplication myApplication;
    private int max;
    private Context context;
    private Thread lyricThread;
    private MessageReciver messageReceiver;
    private LyricInfo lyricInfo; //当前播放的歌曲信息
    private String temptitle = "";
    private File file;
    private File[] files;
    private ImageView bottomHead;
    private PicHandler picHandler;


    public ViewPagerAdapter(List<View> list, final Context context) {
        this.list = list;

        View view = list.get(0);
        View viewlyric = list.get(1);

        picHandler = new PicHandler();
        bottomtitle = (TextView) view.findViewById(R.id.bottom_title);
        bottomnext = (ImageButton) view.findViewById(R.id.bottom_next);
        bottomsinger = (TextView) view.findViewById(R.id.bottomsinger);
        bottomSeekbar = (SeekBar) view.findViewById(R.id.bottom_seekbar);
        bottomplay_pause = (ImageButton) view.findViewById(R.id.bottom_play_pause);
        bottomprivious = (ImageButton) view.findViewById(R.id.bottom_privious);
        bottomHead = (ImageView) view.findViewById(R.id.bottom_head);
        lyric1 = (TextView) viewlyric.findViewById(R.id.lyric1);
        lyric2 = (TextView) viewlyric.findViewById(R.id.lyric2);
        this.context = context;

        myApplication = MyApplication.getApplication();

        lyricInfo = new LyricInfo();
        lyricInfo.lineinfo = new ArrayList<>();

        file = myApplication.getFile();
        files = file.listFiles();

        IntentFilter intentFilter = new IntentFilter();
        messageReceiver = new MessageReciver();
        intentFilter.addAction("com.example.MusicService.PROGRESS");
        intentFilter.addAction("com.example.MusicService.DETIAL");
        intentFilter.addAction("CHANGEMAINBUTTON");
        intentFilter.addAction("TOAST");
        context.registerReceiver(messageReceiver, intentFilter);
        setThread();

        bottomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //seekbar的监听器
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myApplication.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {     //按下进度条 先调用onStartTrackingTouch一次，再调用onProgressChanged一次
                seekBar.setMax(max);
                if (!myApplication.isPlay()) {
                    myApplication.setIsPlay(true);
                }
                myApplication.setIsSeekBarTouch(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                intent.putExtra("PROGRESS", seekBar.getProgress());
                intent.putExtra("SEEK", true);

                Intent intent1 = new Intent("CHANGEMAINBUTTON");

                context.sendBroadcast(intent1);
                context.sendBroadcast(intent);
                myApplication.setIsSeekBarTouch(false);
            }
        });

        bottomnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApplication.setIsPlay(true);
                bottomSeekbar.setProgress(0);
                Intent intentnext = new Intent("com.example.MainActivity.STARTMUSIC");
                intentnext.putExtra("NEXT", true);
                context.sendBroadcast(intentnext);
                Intent intentchange = new Intent("CHANGEMAINBUTTON");
                context.sendBroadcast(intentchange);
            }
        });

        bottomprivious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApplication.setIsPlay(true);
                bottomSeekbar.setProgress(0);
                Intent intentpre = new Intent("com.example.MainActivity.STARTMUSIC");
                intentpre.putExtra("PRE", true);
                context.sendBroadcast(intentpre);
                Intent intentchange = new Intent("CHANGEMAINBUTTON");
                context.sendBroadcast(intentchange);

            }
        });

        bottomplay_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentnotify1 = new Intent("notification_play_pause");
                context.sendBroadcast(intentnotify1);
            }
        });


    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        list.remove(list.get(position));
        context.unregisterReceiver(messageReceiver);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    class MessageReciver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

            if (intent.getAction().equals("PIC")) {
                getBitmap(intent.getStringExtra("URL"));
            }


            if (intent.getAction().equals("CHANGEMAINBUTTON")) {
                if (myApplication.isPlay())
                    bottomplay_pause.setImageResource(R.drawable.pausebule);
                else
                    bottomplay_pause.setImageResource(R.drawable.playblue);
            }

            if (intent.getAction().equals("com.example.MusicService.DETIAL")) {
                bottomsinger.setText(myApplication.getBottomSinger());
                bottomtitle.setText(myApplication.getBottomTitle());
                max = myApplication.getSeekBarMax();
                bottomSeekbar.setMax(max);
            }


            if (intent.getAction().equals("com.example.MusicService.PROGRESS") || intent.getAction().equals("com.example.MainActivity.STARTMUSIC")) {
                bottomSeekbar.setProgress(myApplication.getProgress());
                try {
                    if (!temptitle.equals(bottomtitle.getText().toString())) {
                        temptitle = bottomtitle.getText().toString();

                        data = myApplication.getData();
                        lyric1.setText(temptitle);
                        lyric2.setText("正在搜索歌词，请稍等");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                String title = bottomtitle.getText().toString();
                                final String singer = bottomsinger.getText().toString();
                                String lyric;
                                SongDataGetter songDataGetter;
                                ArrayList<Hash> hashes = (ArrayList<Hash>) SongGetter.getAllSong(title);
                                lyricThread.interrupt();

                                if (hashes != null)
                                    for (int j = 0 ; j < hashes.size() ;j++) {
                                        if (hashes.get(j).getSingerName().contains(singer)) {
                                            songDataGetter = SongGetter.getSongData(hashes.get(j).getFileHash());
                                            songData = songDataGetter.getData();
                                            lyric = songData.getLyrics();
                                            if (!seekLyric()) {
                                                try {
                                                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "//MyLyric//" + title + ".lrc");
                                                    if (!file.exists()) {
                                                        file.createNewFile();
                                                        FileOutputStream fos = new FileOutputStream(file);
                                                        fos.write(lyric.getBytes());
                                                        fos.close();
                                                        seekLyric(); // 找到歌词播放
                                                        break;
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            break;
                                        }

                                        if(j == hashes.size()-1){
                                            Message msg =   new Message() ;
                                            msg.obj = "找不到歌词";
                                            handler2.sendMessage(msg);
                                        }
                                    }

                                myApplication.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(songData != null&&songData.getAudio_name().contains(singer)) {
                                            Glide.with(context)
                                                    .load(songData.getImg())
                                                    .error(R.drawable.ic_changpian_player)
                                                    .into(bottomHead);
                                        }else{
                                            Glide.with(context)
                                                    .load(R.drawable.changpian)
                                                    .into(bottomHead);
                                        }
                                    }
                                });

                            }
                        }).start();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private static Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lyric1.setText(msg.obj.toString());
        }
    };

    private static Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lyric2.setText(msg.obj.toString());
        }
    };

    private void setThread() {
        lyricThread = new Thread(new Runnable() { //处理歌词的线程
            @Override
            public void run() {
                try {
                    int temp = 0;
                    while (temp < lyricInfo.lineinfo.size() - 1) {
                        Message message1 = new Message();
                        Message message2 = new Message();
                        message1.obj = "";
                        message2.obj = "";
                        Thread.sleep(300);

                        for (int j = 0; j < lyricInfo.lineinfo.size() - 1; j++) {

                            if (myApplication.getProgress() >= lyricInfo.lineinfo.get(j).start && myApplication.getProgress() <= lyricInfo.lineinfo.get(j + 1).start) {
                                message1.obj = lyricInfo.lineinfo.get(j).line;
                                message2.obj = lyricInfo.lineinfo.get(j + 1).line;
                                temp = j + 1;
                                break;
                            }
                        }

                        handler1.sendMessage(message1);
                        handler2.sendMessage(message2);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("tag", "线程被打断了");
                    return;
                }
            }
        });
    }


    private boolean seekLyric() {
        files = file.listFiles();
        int pos = 0;

        lyricThread.interrupt();
        for (; pos <= files.length; pos++) {
            try {
                String absoulutePath = files[pos].getAbsolutePath();
                if (files.length > 0 && absoulutePath.contains(temptitle) && !absoulutePath.contains(".mp3")) {
                    Log.e("eee", files[pos].getAbsolutePath());
                    getLRC(files[pos], lyricInfo);   //找到并导入对应歌词到类中
                    setThread();
                    lyricThread.start();
                    return true;  //找到返回true
                }
            } catch (Exception e) {
                return false;
            }

            if (pos == files.length - 1)
                return false;
        }

        return false;
    }

    private void getLRC(File file, LyricInfo lyricinfo) {
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

    private void getBitmap(final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    byte[] pic = response.body().bytes();
                    bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                    Message msg = new Message();
                    msg.obj = bitmap;
                    picHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private class PicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            bottomHead.setImageBitmap((Bitmap) msg.obj);
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
