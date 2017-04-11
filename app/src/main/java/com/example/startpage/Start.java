package com.example.startpage;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.dataBase.MyDataBaseHelper;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Start extends Activity {

    MyApplication myApplication;
    File file;
    private ArrayList<Map<String, String>> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myApplication = (MyApplication) getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initTask task = new initTask();
        task.execute();

        MyDataBaseHelper dpHelper = new MyDataBaseHelper(this,"list.db",null,1);
        SQLiteDatabase dp = dpHelper.getWritableDatabase();
        myApplication.setDp(dp);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);                //隐藏状态栏
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    private class initTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {



            long startTime = System.currentTimeMillis();

            if (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Start.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }//运行权限

            if( ContextCompat.checkSelfPermission(Start.this,Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(Start.this,new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
            }

            String directory = Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() + "/MyLyric/";

            file = new File(directory);

            if (!file.exists())
                file.mkdir();

            myApplication.setFile(file);

            readMusicData();

            myApplication.setData(data);

            while((System.currentTimeMillis() - startTime) < 1000);

            publishProgress();

            while((System.currentTimeMillis() - startTime) < 500);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            finish();
            Intent intent = new Intent("android.intent.action.MAINMUSIC");
            intent.addCategory("android.intent.category.DEFAULT");
            startActivity(intent);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            RelativeLayout yes = (RelativeLayout)findViewById(R.id.yes);
            progressBar.setVisibility(View.GONE);
            yes.setBackgroundResource(R.drawable.yes);

        }
    }


    private void readMusicData() {

        String[] want = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION};

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, want, MediaStore.Audio.Media.DURATION + ">60000", null, MediaStore.Audio.Media.TITLE);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                map.put("title", cursor.getString(0));
                map.put("data", cursor.getString(1));           //读取音乐文件
                map.put("singer", cursor.getString(2));
                map.put("fulltitle", cursor.getString(3));
                map.put("duration", cursor.getInt(4) + "");
                map.put("isplay", false + "");
                map.put("URL", "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=" + map.get("title") + "&duration=" + map.get("duration") + "&hash=");
                data.add(map);


            } while (cursor.moveToNext());
        }

            Log.e("start",data.size()+"");
        for(int i = 0 ; i < data.size()  ; i++){
            data.get(i).put("position",i+"");
        }

        myApplication.setData(data);

        if (cursor != null)
            cursor.close();
    }


}


