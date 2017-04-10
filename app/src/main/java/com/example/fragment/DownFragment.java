package com.example.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.example.song.Hash;
import com.example.song.SongGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class DownFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pagerdownlist,container,false);

        MyApplication myApplication = MyApplication.getApplication();
        final List<Hash> hashes = myApplication.getHashList();
        List<String> titles = new ArrayList<>();

        for(Hash e : hashes){
            titles.add(e.getFileName());
        }

        ListView listView = (ListView)view.findViewById(R.id.PAGE_DOWN);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(adapter);

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
