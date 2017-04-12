package com.example.song;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class SongData {

    private String audio_name;
    private String album_name;
    private String img;
    private String author_name;
    private String song_name;
    private String lyrics;
    private String play_url;
    private String timelength;

    public String getTimelength() {
        return timelength;
    }


    public String getAlbum_name() {
        return album_name;
    }

    public String getAudio_name() {
        return audio_name;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getImg() {
        return img;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getPlay_url() {
        return play_url;
    }

    public String getSong_name() {
        return song_name;
    }
}
