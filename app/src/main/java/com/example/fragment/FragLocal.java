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

    String mode = "orl";
    ImageButton local_mode_bt;
    ArrayList<Map<String, String>> data;
    MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", MODE_PRIVATE);
            mode = sharedPreferences.getString("MODE", "rlo");
            if (mode.charAt(2) == 'o') local_mode_bt.setImageResource((R.drawable.orderplay));
            if (mode.charAt(2) == 'r') local_mode_bt.setImageResource((R.drawable.randomblue));
            if (mode.charAt(2) == 'l') local_mode_bt.setImageResource((R.drawable.loopplaybule));

            Intent modeintent = new Intent("com.example.LocalMusic.MODE");
            modeintent.putExtra("MODE", mode.charAt(2));
            getActivity().sendBroadcast(modeintent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("info", "didnt' find");
        }


        View view = inflater.inflate(R.layout.fraglocal,container,false);


        local_mode_bt = (ImageButton) view.findViewById(R.id.local_mode_bt);
        myApplication = MyApplication.getApplication();
        data = myApplication.getData();


        MySimpleAdapter simpleAdapter = new MySimpleAdapter(getContext(), data, R.layout.listitem,
                new int[]{R.id.local_list_title,R.id.local_SingerName,R.id.local_list_add,R.id.local_list_like,R.id.local_list_del,R.id.local_list__button_play});

        ListView listview = (ListView) view.findViewById(R.id.local_music_listview);
        listview.setAdapter(simpleAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        local_mode_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("fragmen","destory");
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("MODE", mode);
        editor.apply();
    }

    void setMode() {
        Intent modeintent = new Intent("com.example.LocalMusic.MODE");
        if (mode.charAt(0) == 'o') {
            local_mode_bt.setImageResource(R.drawable.orderplay);
            modeintent.putExtra("MODE", 'o');
            getActivity().sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'o';
        } else if (mode.charAt(0) == 'r') {
            local_mode_bt.setImageResource((R.drawable.randomblue));
            modeintent.putExtra("MODE", 'r');
            getActivity().sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'r';
        } else if (mode.charAt(0) == 'l') {
            local_mode_bt.setImageResource((R.drawable.loopplaybule));
            modeintent.putExtra("MODE", 'l');
            getActivity().sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'l';
        }


    }

}
