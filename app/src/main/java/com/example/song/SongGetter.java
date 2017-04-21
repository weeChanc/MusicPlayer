package com.example.song;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.View.MLog;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class SongGetter {

    static OkHttpClient client = new OkHttpClient();

    static Gson gson = new Gson();
    static Response response;

    public static List<Hash> getAllSong(String name) {

        String ListURL = "http://songsearch.kugou.com/song_search_v2?callback=jQuery191013413509052461192_1491829959432&keyword=" + name
                + "&page=1&pagesize=30&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1491829959434";
        Data data = null;

        try {
            Request request = new Request.Builder().url(ListURL).build();
            gson = new Gson();

            response = client.newCall(request).execute();
            String dataFromJason = response.body().string();
            data = gson.fromJson(dataFromJason.substring(dataFromJason.indexOf('{'), dataFromJason.lastIndexOf('}') + 1), Data.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;  //处理json返回空的情况
        }

        return data.getData().getHashList();
    }

    public static String download(String hash) {
        try {
            String MessageURL = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + hash + "&album_id=&_=1491830054690";
            Request request = new Request.Builder().url(MessageURL).build();

            response = client.newCall(request).execute();
            SongDataGetter songdata = gson.fromJson(response.body().string(), SongDataGetter.class);

            String path = MyApplication.getApplication().getFile().getPath() + "/" + songdata.getData().getAudio_name() + ".mp3";
            File file = new File(path);
            if (!file.exists()) {
                String download = songdata.getData().getPlay_url();
                request = new Request.Builder().url(download).build();
                response = client.newCall(request).execute();
                byte[] song = response.body().bytes();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(song);
                fos.close();

            } else {
                return "exist";
            }

            return path; //返回对应文件路径

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static SongDataGetter getSongData(String hash) {
        SongDataGetter songdata = null;
        try {
            String MessageURL = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + hash + "&album_id=&_=1491830054690";
            Request request = new Request.Builder().url(MessageURL).build();
            response = client.newCall(request).execute();
            songdata = gson.fromJson(response.body().string(), SongDataGetter.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return songdata;
    }


}
