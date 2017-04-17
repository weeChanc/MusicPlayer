package com.example.MyAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.example.song.Hash;
import com.example.song.SongGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class DownLoadListAdapter extends BaseAdapter {

    private ArrayList<Hash> resource;
    private Context context;
    private int[] to;
    private int layoutID;
    private String title;
    private String singer;


    public DownLoadListAdapter(Context context, ArrayList<Hash> resource, int layoutID, int[] to) {
        super();
        this.context = context;
        this.resource = resource;
        this.layoutID = layoutID;
        this.to = to;
    }

    @Override
    public int getCount() {
        return resource.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(layoutID,parent,false);
            viewHolder.down_bt = (ImageButton) view.findViewById(R.id.down_bt);
            viewHolder.down_singer = (TextView) view.findViewById(R.id.down_singer);
            viewHolder.down_title = (TextView) view.findViewById(R.id.item_title);
            viewHolder.down_play = (Button)view.findViewById(R.id.down_play);
            view.setTag(viewHolder);

        }else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }


        title = resource.get(position).getSongName();
        title = title.replaceAll("<em>","");
        title = title.replaceAll("</em>","");
        viewHolder.down_title.setText(title);

        singer = resource.get(position).getSingerName();  //去除异常的字符串

        singer = singer.replaceAll("<em>","");
        singer = singer.replaceAll("</em>","");
        viewHolder.down_singer.setText(singer);

        viewHolder.down_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SongGetter.download(resource.get(position).getFileHash(),resource.get(position).getFileName().replaceAll("<em>","").replaceAll("</em>",""));
                    }
                }).start();
            }
        });

        viewHolder.down_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String path;
                        MyApplication myApplication = MyApplication.getApplication();
                        ArrayList<Map<String,String >> data = myApplication.getData();

                        path = SongGetter.download(resource.get(position).getFileHash(),"准备播放: " +resource.get(position).getFileName().replaceAll("<em>","").replaceAll("</em>",""));


                        Map<String,String> map = new HashMap<>();
                        map.put("title",resource.get(position).getSongName().replaceAll("<em>","").replaceAll("</em>",""));
                        map.put("data",path);
                        map.put("singer",resource.get(position).getSingerName().replaceAll("<em>","").replaceAll("</em>",""));
                        map.put("fulltitle",resource.get(position).getFileName().replaceAll("<em>","").replaceAll("</em>",""));
                        map.put("duration",SongGetter.getSongData(resource.get(position).getFileHash()).getData().getTimelength());
                        map.put("position",data.size()+"");

                        data.add(map);

                        myApplication.setFinaldata( data );
                        myApplication.setIsPlay(true);

                        myApplication.setPosition(position);
                        myApplication.setIsPlay(true);
                        myApplication.getThread().interrupt();
                        Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                        intent.putExtra("POSITION",true);
                        intent.putExtra("LOCATION",data.size()-1);
                        context.sendBroadcast(intent);
                        Intent intent2 = new Intent("notification_play_pause");
                        intent2.putExtra("LIST", true);
                        context.sendBroadcast(intent2);


                    }
                }).start();
            }
        });


        return view;
    }

    class ViewHolder{
        ImageButton down_bt;
        TextView down_title;
        TextView down_singer;
        Button down_play;
    }
}
