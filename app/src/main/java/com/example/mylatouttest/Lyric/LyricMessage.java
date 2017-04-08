package com.example.mylatouttest.Lyric;

/**
 * Created by 铖哥 on 2017/4/8.
 */

public class LyricMessage {

    private String accesskey;
    private String id;
    private String singer;
    private  String URL ;

    public String getURL() {
        return URL;
    }

    public String getAccessKey() {
        return accesskey;
    }

    public String getId() {
        return id;
    }

    public String getSinger() {
        return singer;
    }

    public String initURL(){
        URL="http://lyrics.kugou.com/download?ver=1&client=pc&id="+id+"&accesskey="+accesskey+"&fmt=lrc&charset=utf8";
        return URL;
    }
}


