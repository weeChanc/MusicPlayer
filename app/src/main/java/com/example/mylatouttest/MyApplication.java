package com.example.mylatouttest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.MyAdapter.DownLoadListAdapter;
import com.example.MyAdapter.MySimpleAdapter;
import com.example.dataBase.MyDataBaseHelper;
import com.example.song.Hash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/7.
 */

/**
 * 全局变量
 */

public class MyApplication  extends Application{
    public static final int ORDER = 1;
    private  static MyApplication myApplication;
    private static Context context;
    private  ArrayList<Map<String, String>> data ;
    private ArrayList<Map<String, String>> finaldata ;
    private ArrayList<Integer> pos =  new ArrayList<>(); //

    private boolean isPlay = false ;
    private File[] files;
    private int seekBarMax;
    private boolean isSeekBarTouch = false;
    private String bottomTitle;
    private int position = 0 ;
    private int progress;
    private String bottomSinger;
    private File file;
    private SQLiteDatabase dp;
    private Activity activity;
    private int play_mode;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Integer> getPos() {
        return pos;
    }

    public ArrayList<Map<String, String>> getFinaldata() {
        return finaldata;
    }

    public void setFinaldata(ArrayList<Map<String, String>> finaldata) {
        this.finaldata = finaldata;
    }

    public int getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public static Context getContext() {
        return context;
    }

    public void setDp(SQLiteDatabase dp) {
        this.dp = dp;
    }

    public SQLiteDatabase getDp() {
        return dp;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getBottomSinger() {
        return bottomSinger;
    }

    public void setBottomSinger(String bottomSinger) {
        this.bottomSinger = bottomSinger;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setBottomTitle(String bottomTitle){
        this.bottomTitle = bottomTitle;
    }

    public void setSeekBarMax(int seekBarMax){
        this.seekBarMax = seekBarMax;
    }


    public void setIsPlay(boolean isPlay){
        this.isPlay = isPlay;
    }


    public void setIsSeekBarTouch(boolean isSeekBarTouch){
        this.isSeekBarTouch = isSeekBarTouch;
    }

    public void setData(ArrayList<Map<String, String>> data){
        this.data = data;
    }


    public static MyApplication getApplication(){
        return myApplication;
    }

    public  ArrayList<Map<String, String>> getData(){
        return data;
    }

    public boolean isPlay(){
        return isPlay;
    }

    public boolean isSeekBarTouch(){
        return isSeekBarTouch;
    }

    public int getSeekBarMax(){
        return seekBarMax;
    }

    public String getBottomTitle(){
        return bottomTitle;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        context = getApplicationContext();
    }
}
