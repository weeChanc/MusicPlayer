package com.example.fragment;

import android.app.Activity;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * 最近播放列表
 */

public class FragRecent extends Fragment {
    MySimpleAdapter mySimpleAdapter;
    MyApplication myApplication = MyApplication.getApplication();
    ArrayList<Map<String, String>> data = new ArrayList<>();
    ImageButton back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragrecent, container, false);

        SQLiteDatabase db = myApplication.getDp();
        Cursor cursor = db.query("Recent", null, null, null, null, null, null, null);

        if (cursor.moveToLast()) {
            int i = 1;
            do {
                if (new File(cursor.getString(cursor.getColumnIndex("data"))).exists()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("singer", cursor.getString(cursor.getColumnIndex("singer")));
                    map.put("title", cursor.getString(cursor.getColumnIndex("title")));
                    map.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                    map.put("data", cursor.getString(cursor.getColumnIndex("data")));
                    map.put("isChecked", "false");
                    data.add(map);
                    if (i++ == 20) {
                        break;   //最多截取20首
                    }
                }
            } while (cursor.moveToPrevious());

        }
        cursor.close();

        myApplication.setRecentdata(data);

        mySimpleAdapter = new MySimpleAdapter(getContext(), data, R.layout.listitem);
        ListView listView = (ListView) view.findViewById(R.id.like_listview);
        listView.setAdapter(mySimpleAdapter);

        back = (ImageButton) view.findViewById(R.id.recent_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }


}


