package com.example.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/11.
 */

public class FragRecent extends Fragment {
    MySimpleAdapter mySimpleAdapter;
    MyApplication myApplication = MyApplication.getApplication();
    ArrayList<Map<String, String>> data;
    RecentReceiver recentReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragrecent, container, false);

        SQLiteDatabase db = myApplication.getDp();
        data = new ArrayList<>();
        Cursor cursor = db.query("Recent", null, null, null, null, null, null, null);

        if (cursor.moveToLast()) {
            int i = 1;
            do {
                Map<String, String> map = new HashMap();
                map.put("singer", cursor.getString(cursor.getColumnIndex("singer")));
                map.put("title", cursor.getString(cursor.getColumnIndex("title")));
                map.put("position", cursor.getString(cursor.getColumnIndex("position")));
                data.add(map);
                if(i++ == 20){
                    break;   //最多截取20首
                }
            } while (cursor.moveToPrevious());
        }


        mySimpleAdapter = new MySimpleAdapter(getContext(), data, R.layout.listitem,
                        new int[]{R.id.down_title, R.id.down_singer, R.id.local_list_add, R.id.local_list_like, R.id.local_list_del, R.id.local_list__button_play});

        ListView listView = (ListView) view.findViewById(R.id.like_listview);
        listView.setAdapter(mySimpleAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ChangeRecent");

        recentReceiver = new RecentReceiver();
        activity.registerReceiver(recentReceiver,intentFilter);
    }

    class RecentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ChangeRecent")) {
                Log.e("tag","sdfasdfasdfasdfsdaf");
                int position = myApplication.getPosition();
                Map<String,String> map = new HashMap<>();
                map.put("singer",myApplication.getData().get(position).get("singer"));
                map.put("title",myApplication.getData().get(position).get("title"));
                map.put("position",position+"");
                for(int i = 0 ; i < data.size() ; i++)
                {
                    if(data.get(i).get("position").equals(position+"")) {
                        data.remove(i);
                        break;
                    }
                    if(i == 19){
                        data.remove(19);  //控制历史记录的数量
                        break;
                    }
                }

                data.add(0,map);
                mySimpleAdapter.notifyDataSetChanged();

            }
        }
    }

}


