package com.example.mylatouttest;

import android.app.Application;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/7.
 */

public class MyApplication  extends Application{

    private  static MyApplication myApplication;
    private  ArrayList<Map<String, String>> data;
    private boolean isPlay = false ;
    private File[] files;
    private int seekBarMax;
    private boolean isSeekBarTouch = false;
    private String mode;
    private String bottomTitle;
    private int position = 0 ;
    private int progress;
    private String bottomSinger;
    private File file;
    private Thread thread;

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
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

    public void setFiles(File[] files){
        this.files = files;
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

    public File[] getFiles(){
        return files;
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
    }
}
