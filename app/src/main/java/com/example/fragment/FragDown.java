package com.example.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.MyAdapter.DownLoadListAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.example.song.Hash;
import com.example.song.SongGetter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 铖哥 on 2017/4/10.
 */

public class FragDown extends Fragment {


    EditText editText;
    Button button;
    ListView listView;
    ArrayList<Hash> hashes;
    List<String> titles;
    MyApplication myApplication = MyApplication.getApplication();
    DownLoadListAdapter downLoadListAdapter;
    ArrayAdapter<String> adapter;

    public Handler  handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            downLoadListAdapter = new DownLoadListAdapter(getContext(),hashes,R.layout.downitem,new int[]{R.id.down_title,R.id.down_singer,R.id.down_bt});
            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, titles);
            listView.setAdapter(downLoadListAdapter);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        titles.clear();
                        hashes = (ArrayList<Hash>) SongGetter.getAllSong(editText.getText().toString());

                        for(int i = 0 ; i < hashes.size() ; i++) {
                            Log.e("tag", hashes.get(i).getFileName());
                            titles.add(hashes.get(i).getFileName());
                        }

                        handler.sendEmptyMessage(0);
                    }
                }).start();



            }

        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragdown,container,false);

         editText = (EditText) view.findViewById(R.id.edit);
         button = (Button) view.findViewById(R.id.button2);
         listView = (ListView) view.findViewById(R.id.down_list);
         hashes = new ArrayList<>();
         titles = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SongGetter.download(hashes.get(position).getFileHash());
                    }
                }).start();
            }
        });

        return view;
    }
}
