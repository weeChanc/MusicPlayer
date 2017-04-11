package com.example.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.MyAdapter.MySimpleAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 铖哥 on 2017/4/11.
 */

public class FragLocal extends Fragment {


    ArrayList<Map<String, String>> data;
    MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fraglocal,container,false);
        myApplication = MyApplication.getApplication();
        data = myApplication.getData();

        MySimpleAdapter simpleAdapter = new MySimpleAdapter(getContext(), data, R.layout.listitem,
                new int[]{R.id.local_list_title,R.id.local_SingerName,R.id.local_list_add,R.id.local_list_like,R.id.local_list_del,R.id.local_list__button_play});

        ListView listview = (ListView) view.findViewById(R.id.local_music_listview);
        listview.setDivider(null);
        listview.setAdapter(simpleAdapter);


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("fragmen","destory");
    }

}
