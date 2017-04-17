package com.example.startpage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.dataBase.MyDataBaseHelper;
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
    private ArrayList<Map<String, String>> finaldata = new ArrayList<>();
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Start.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }//运行权限

        if( ContextCompat.checkSelfPermission(Start.this,Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(Start.this,new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
        }

        while (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED);
        myApplication = (MyApplication) getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        MyDataBaseHelper dpHelper = new MyDataBaseHelper(this,"list.db",null,1);
        SQLiteDatabase dp = dpHelper.getWritableDatabase();
        myApplication.setDp(dp);
        imageView = (ImageView) findViewById(R.id.im);
        imageView.setImageResource(R.drawable.ic_start);


        initTask task = new initTask();
        task.execute();

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




            String directory = Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() + "/MyLyric/";

            file = new File(directory);

            if (!file.exists())
                file.mkdir();

            myApplication.setFile(file);

            readMusicData();
            readLovePos();
//            readPlayTime();

            myApplication.setData(data);



            while((System.currentTimeMillis() - startTime) < 1000);

            publishProgress();

            while((System.currentTimeMillis() - startTime) < 500);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            SharedPreferences share = getSharedPreferences("data", MODE_PRIVATE);
            int mode = share.getInt("MODE",-1);
            if(mode == -1) {
                myApplication.setPlay_mode(MyApplication.ORDER);
                Intent intent = new Intent("android.intent.action.WELCOME");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//若没有数据则是第一次打开  开始导航页面
                startActivity(intent);
                share.edit().putInt("MODE",MyApplication.ORDER).apply();
            }else {

                myApplication.setPlay_mode(mode);   //设置上次退出的选择模式
                Intent intent = new Intent("android.intent.action.MAINMUSIC");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addCategory("android.intent.category.DEFAULT");
                startActivity(intent);
                finish();
            }
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
        if (cursor.moveToFirst() && cursor!=null) {
//            int i = 0;
            do {
//                if(i++ == 10 ) break;
                Map<String, String> map = new HashMap<>();
                map.put("title", cursor.getString(0));
                map.put("data", cursor.getString(1));           //读取音乐文件
                map.put("singer", cursor.getString(2));
                map.put("fulltitle", cursor.getString(3));
                map.put("duration", cursor.getInt(4) + "");
                map.put("isplay", "F");
                data.add(map);
                finaldata.add(map);

            } while (cursor.moveToNext());
            cursor.close();

            if(data.size()==0){
                Map<String, String> map = new HashMap<>();
                map.put("title","找不到本地歌曲");
                map.put("data", "");           //读取音乐文件
                map.put("singer", "");
                map.put("fulltitle", "");
                map.put("duration", "0");
                data.add(map);
                finaldata.add(map);
            }

        }

        for(int i = 0 ; i < data.size()  ; i++){
            data.get(i).put("position",i+"");
            finaldata.get(i).put("position",i+"");
        }

        myApplication.setData(data);
        myApplication.setFinaldata(finaldata);
    }

    private void readLovePos(){

        ArrayList<Integer> pos = myApplication.getPos();
//        ArrayList<String> lover = myApplication.getLover();
        SQLiteDatabase db = myApplication.getDp();

        Cursor cursor = db.query("Like", null, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                pos.add(cursor.getInt(cursor.getColumnIndex("position")));
//                lover.add(cursor.getString(cursor.getColumnIndex("singer")));
            }while(cursor.moveToNext());
        }

        cursor.close();
    }

//    private void readPlayTime(){
//        SQLiteDatabase db = myApplication.getDp();
//        Cursor cursor = db.query("Recent", null, null, null, null, null, null,"id DESC");
//        if(cursor.moveToFirst()){
//            int time =  0;
//            String a = "a";
//            String b = "b";
//            int i = 0 ;
//            do{
//                 if(i++ > 2000) break;
//                 a = cursor.getColumnName(cursor.getColumnIndex("title"));
//                 if(cursor.moveToNext())break;
//                 b = cursor.getColumnName(cursor.getColumnIndex("title"));
//
//                if(a.equals(b)){
//                    time++;
//                }else{
//                    if(time >= 5){
//                        time = 0;
//                        lover.add(a);
//                    }
//                }
//
//            }while(true);
//            cursor.close();
//
//            myApplication.setLover(lover);
//
//        }
//
//    }


}


