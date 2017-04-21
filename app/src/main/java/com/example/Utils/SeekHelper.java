package com.example.Utils;

import com.example.MyAdapter.ViewPagerAdapter;

import java.util.List;

/**
 * Created by 铖哥 on 2017/4/21.
 */

public class SeekHelper {

    static int temp;

    public static int seekLine(List<ViewPagerAdapter.LineInfo> lineinfo, int progress) {


        return seek(lineinfo, progress, 0, lineinfo.size() - 1);
    }

    public static int seek(List<ViewPagerAdapter.LineInfo> lineinfo, int progress, int low, int hight) {

        int mid = (low + hight) / 2;

        if (hight - low == 1 || low == hight)
            return low;

        if (lineinfo.get(mid).start < progress) {
            temp = seek(lineinfo, progress, mid, hight);
        } else {
            temp = seek(lineinfo, progress, low, mid);
        }

        return temp;
    }

}
