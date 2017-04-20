package com.example.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.mylatouttest.MyApplication;

/**
 * Created by 铖哥 on 2017/4/20.
 */

public class ToastHelper  {

    public static Toast mToast;
    public static String lastStr;



    public static void showToast(String str){
        if (mToast == null  ){
            lastStr = str;
            mToast = Toast.makeText(MyApplication.getApplication().getActivity(),str,Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
                mToast.show();

    }
}
