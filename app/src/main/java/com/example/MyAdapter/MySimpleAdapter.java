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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import org.w3c.dom.Text;

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
    private MyApplication myApplication = MyApplication.getApplication();


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

        View view;
        ViewHolder viewHolder;

        if (convertView == null) {

            view = inflater.inflate(layoutID, parent, false);
            viewHolder = new ViewHolder();


                    viewHolder.title_tv = (TextView) view.findViewById(R.id.local_list_title);
                    viewHolder.singer_tv = (TextView) view.findViewById(R.id.local_SingerName);
                    viewHolder.bt1 = (ImageButton)view.findViewById(R.id.local_list_add);
                    viewHolder.bt2 = (ImageButton)view.findViewById(R.id.local_list_like);
                    viewHolder.bt3 = (ImageButton)view.findViewById(R.id.local_list_del);
                    viewHolder.bt4 = (Button)view.findViewById(R.id.local_list__button_play);


                    viewHolder.title_tv.setText(resource.get(position).get("title"));
                    viewHolder.singer_tv.setText(resource.get(position).get("singer"));
                    addListener(viewHolder);

                    view.setTag(viewHolder);

            viewHolder.bt4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myApplication.setPosition(position);
                    Log.e("info", "play");
                    Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                    intent.putExtra("LOCATION", position);
                    intent.putExtra("POSITION", true);                             //下面的方法找不到对应的position只能放上来
                    context.sendBroadcast(intent);
                    Intent intent1 = new Intent("com.example.LocalMusic.PLAY"); //点击后通知主界面更新图标
                    context.sendBroadcast(intent1);
                }
            });


        }
        else {
            view = convertView;

            viewHolder = (ViewHolder) view.getTag();

            viewHolder.singer_tv.setText(resource.get(position).get("singer"));
            viewHolder.title_tv.setText(resource.get(position).get("title"));

            addListener(viewHolder);

            viewHolder.bt4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myApplication.setPosition(position);

                    myApplication.setThreadstatus(false);
                    Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                    intent.putExtra("LOCATION", position);
                    intent.putExtra("POSITION", true);                             //下面的方法找不到对应的position只能放上来
                    context.sendBroadcast(intent);
                    Intent intent1 = new Intent("com.example.LocalMusic.PLAY"); //点击后通知主界面更新图标
                    context.sendBroadcast(intent1);
                    myApplication.setThreadstatus(true);
                }
            });

        }

            return view;
    }


    class ViewHolder{
        TextView title_tv;
        TextView singer_tv;
        ImageButton bt1;
        ImageButton bt2;
        ImageButton bt3;
        Button bt4;
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

        view.findViewById(R.id.local_list__button_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("info","play");
            }
        });


    }

        private void addListener (ViewHolder viewHolder){
            viewHolder.bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("info", "add");
                }
            });

            viewHolder.bt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("info", "love");
                }
            });

            viewHolder.bt3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("info", "delete");
                }
            });


    }


}
