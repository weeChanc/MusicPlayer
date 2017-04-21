package com.example.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.MyAdapter.MySimpleAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/11.
 */


/**
 * 该fragment用于显示本地音乐的列表
 */

public class FragLocal extends Fragment {


    ArrayList<Map<String, String>> data;
    MyApplication myApplication;
    ImageButton back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fraglocal,container,false);
        myApplication = MyApplication.getApplication();

        SQLiteDatabase db = myApplication.getDp();
        data = new ArrayList<>();
        myApplication.setFinaldata(data);
        Cursor cursor = db.query("MyMusic",null,null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do{
                if( new File(cursor.getString(cursor.getColumnIndex("data"))).exists()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("singer", cursor.getString(cursor.getColumnIndex("singer")));
                    map.put("title", cursor.getString(cursor.getColumnIndex("title")));
                    map.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                    map.put("data", cursor.getString(cursor.getColumnIndex("data")));
                    map.put("isChecked", "false");

                    data.add(map);
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        //从数据库从读取数据转化为数据源

        MySimpleAdapter simpleAdapter = new MySimpleAdapter(getContext(),data, R.layout.listitem);

        ListView listview = (ListView) view.findViewById(R.id.local_music_listview);
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(getContext(),R.anim.spin));
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);   //为listview每个item出现设置动画

        listview.setLayoutAnimation(lac);
        listview.setAdapter(simpleAdapter);

        back = (ImageButton) view.findViewById(R.id.local_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

}
