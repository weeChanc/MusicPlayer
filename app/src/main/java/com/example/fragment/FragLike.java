package com.example.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/10.
 */

/**
 *该fragmnet用于显示我喜欢的列表
 */

public class FragLike extends Fragment  {

    MyApplication myApplication = MyApplication.getApplication();
    ArrayList<Map<String,String>> data ;
    ImageButton back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fraglike,container,false);

        SQLiteDatabase db = myApplication.getDp();
        data = new ArrayList<>();
        Cursor cursor = db.query("Like",null,null,null,null,null,null,null);

        if(cursor.moveToLast()){
            do{
                Map<String,String> map = new HashMap<>();
                map.put("singer",cursor.getString(cursor.getColumnIndex("singer")));
                map.put("title",cursor.getString(cursor.getColumnIndex("title")));
                map.put("position",cursor.getString(cursor.getColumnIndex("position")));
                map.put("duration",cursor.getString(cursor.getColumnIndex("duration")));
                map.put("data",cursor.getString(cursor.getColumnIndex("data")));
                map.put("like","T");

                data.add(map);
            }while(cursor.moveToPrevious());
        }
        cursor.close();
        //从数据库从读取数据转化为数据源

        myApplication.setLikedata(data);
        MySimpleAdapter mySimpleAdapter =
                new MySimpleAdapter(getContext(),data,R.layout.listitem);

        ListView listView = (ListView) view.findViewById(R.id.like_listview);
        listView.setAdapter(mySimpleAdapter);

        back = (ImageButton) view.findViewById(R.id.like_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}
