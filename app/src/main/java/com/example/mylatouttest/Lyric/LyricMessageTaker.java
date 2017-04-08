package com.example.mylatouttest.Lyric;

import com.example.mylatouttest.Lyric.LyricMessage;

import java.util.List;

/**
 * Created by 铖哥 on 2017/4/8.
 */

public class LyricMessageTaker {
    private List<LyricMessage> candidates;

    public void setCandidates(List<LyricMessage> candidates) {
        this.candidates = candidates;
    }

    public List<LyricMessage> getCandidates() {
        return candidates;
    }
}
