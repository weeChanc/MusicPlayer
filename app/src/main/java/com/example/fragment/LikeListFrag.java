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
import android.widget.ListView;

import com.example.MyAdapter.MySimpleAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class LikeListFrag extends Fragment  {

    MyApplication myApplication = MyApplication.getApplication();
    ArrayList<Map<String,String>> data ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.likelist,container,false);


        SQLiteDatabase db = myApplication.getDp();
        data = new ArrayList<>();
        Cursor cursor = db.query("Like",null,null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do{
                Map<String,String> map = new HashMap();
                map.put("singer",cursor.getString(cursor.getColumnIndex("singer")));
                map.put("title",cursor.getString(cursor.getColumnIndex("title")));
                map.put("position",cursor.getString(cursor.getColumnIndex("position")));

                data.add(map);
            }while(cursor.moveToNext());
        }

        MySimpleAdapter mySimpleAdapter =
                new MySimpleAdapter(getContext(),data,R.layout.listitem,
                        new int[]{R.id.local_list_title,R.id.local_SingerName,R.id.local_list_add,R.id.local_list_like,R.id.local_list_del,R.id.local_list__button_play});

        ListView listView = (ListView) view.findViewById(R.id.like_listview);
        listView.setAdapter(mySimpleAdapter);


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
