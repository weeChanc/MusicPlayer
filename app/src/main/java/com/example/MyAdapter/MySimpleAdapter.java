package com.example.MyAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.dataBase.MyDataBaseHelper;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/1.
 */

public class MySimpleAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> resource;
    private int layoutID;
    private int[] to;
    private LayoutInflater inflater;
    private MyApplication myApplication = MyApplication.getApplication();
    private Thread lyricThread;
    private ArrayList<Map<String,String>> data;

    public MySimpleAdapter(Context context, List<Map<String, String>> resource, int layoutID,int[] to) {
        this.context = context;
        this.resource = resource;
        this.layoutID = layoutID;
        this.to = to;
        inflater = LayoutInflater.from(context);

        lyricThread=myApplication.getThread();
        data = myApplication.getData();
        Log.e("count",data.size()+"");
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

                    viewHolder.title_tv = (TextView) view.findViewById(to[0]);
                    viewHolder.singer_tv = (TextView) view.findViewById(to[1]);
                    viewHolder.bt1 = (ImageButton)view.findViewById(to[2]);
                    viewHolder.bt2 = (ImageButton)view.findViewById(to[3]);
                    viewHolder.bt3 = (ImageButton)view.findViewById(to[4]);
                    viewHolder.bt4 = (Button)view.findViewById(to[5]);
                    view.setTag(viewHolder);

        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.singer_tv.setText(resource.get(position).get("singer"));
        viewHolder.title_tv.setText(resource.get(position).get("title"));

            addListener(viewHolder);

            viewHolder.bt4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myApplication.setPosition(Integer.parseInt(resource.get(position).get("position")));
                    myApplication.setIsPlay(true);

                    Log.e("tag",resource.get(position).get("position")+"");

                    lyricThread.interrupt();
                    Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                    intent.putExtra("LOCATION", Integer.parseInt(resource.get(position).get("position")));
                    intent.putExtra("POSITION", true);
                    context.sendBroadcast(intent);                              //下面的方法找不到对应的position只能放上来
                    Intent intent1 = new Intent("com.example.LocalMusic.PLAY"); //点击后通知主界面更新图标
                    context.sendBroadcast(intent1);

                    Intent intent2 = new Intent("notification_play_pause");
                    intent2.putExtra("LIST",true);
                    context.sendBroadcast(intent2);


                }
            });

        viewHolder.bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myApplication.getDp();
                Cursor cursor =  db.query("Like",null,null,null,null,null,null,null);
                ContentValues values = new ContentValues();
                values.put("title",data.get(position).get("title"));
                values.put("singer",data.get(position).get("singer"));
                values.put("duration",data.get(position).get("duration"));
                values.put("position",position);

                boolean common = false;
                if(cursor.moveToFirst()) {
                    do {
                        if(cursor.getString(cursor.getColumnIndex("title")).equals(values.get("title")) &&
                                cursor.getString(cursor.getColumnIndex("duration")).equals(values.get("duration"))){
                                    common = true;
                        }
                    }while(cursor.moveToNext());

                }
                if(!common)
                    db.insert("Like",null,values);
                cursor.close();
                }

        });



            return view;
    }


   private class ViewHolder{
        TextView title_tv;
        TextView singer_tv;
        ImageButton bt1;
        ImageButton bt2;
        ImageButton bt3;
        Button bt4;
    }

        private void addListener (ViewHolder viewHolder){
            viewHolder.bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = myApplication.getDp();
                    Cursor cursor =  db.query("Like",null,null,null,null,null,null,null);

                    if(cursor.moveToFirst()){
                        do {
                            Log.e("message",cursor.getString(cursor.getColumnIndex("title")));

                        }while(cursor.moveToNext());
                    }
                    cursor.close();
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
