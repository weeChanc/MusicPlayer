package com.example.View;

import android.util.Log;

/**
 * Created by 铖哥 on 2017/4/9.
 */

public class MLog {

    public static final int VERBOSE = 1;
    public static final int INFO = 3;
    public static final int ERROR = 5;
    public static final int level = 5;

    public static void e (String tag , String msg){
        if(level == 5){
            Log.e(tag,msg);
        }
    }

}
