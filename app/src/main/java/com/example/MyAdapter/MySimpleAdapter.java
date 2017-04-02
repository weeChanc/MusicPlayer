package com.example.MyAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mylatouttest.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/1.
 */

public class MySimpleAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> resource;
    private int layoutID;
    private String[] from;
    private int[] to;
    private LayoutInflater inflater;


    public MySimpleAdapter(Context context, List<Map<String, String>> resource, int layoutID, String[] from, int[] to) {
        this.context = context;
        this.resource = resource;
        this.layoutID = layoutID;
        this.from = from;
        this.to = to;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return resource.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(layoutID, parent, false);

        if (convertView != null) {
            for (int i = 0; i < from.length; i++) {
                if (view.findViewById(to[i]) instanceof TextView) {
                    TextView tv = (TextView) view.findViewById(to[i]);
                    TextView local_singername = (TextView) view.findViewById(R.id.local_SingerName);

                    local_singername.setText(resource.get(position).get("singer"));
                    tv.setText(resource.get(position).get("title"));

//                if (Build.VERSION.SDK_INT >= 21) {
//                    tv.setBackgroundColor(Color.WHITE);
//                    RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.elevation);
//                    relativeLayout.setBackgroundColor(Color.WHITE);
//                }
                } else {
                    view = convertView;
                }
            }
        }

            addListener(view);

            ((Button) view.findViewById(R.id.local_list__button_play)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("info", "play");
                    Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                    intent.putExtra("LOCATION", position);
                    intent.putExtra("POSITION", true);                             //下面的方法找不到对应的position只能放上来
                    context.sendBroadcast(intent);

                    Intent intent1 = new Intent("com.example.LocalMusic.PLAY"); //点击后通知主界面更新图标
                    context.sendBroadcast(intent1);
                }
            });

            return view;
        }

    private void addListener(View view) {

        view.findViewById(R.id.local_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("info", "add");
            }
        });

        view.findViewById(R.id.local_list_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("info", "like");
            }
        });

        view.findViewById(R.id.local_list_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("info", "delete");
            }
        });


    }

}
