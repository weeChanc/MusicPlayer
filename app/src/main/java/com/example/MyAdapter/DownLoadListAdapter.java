package com.example.MyAdapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mylatouttest.R;
import com.example.song.Hash;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class DownLoadListAdapter extends BaseAdapter {

    ArrayList<Hash> resource;
    Context context;
    int[] to;
    int layoutID;
    ImageButton down_bt;
    TextView down_title;
    TextView down_singer;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(layoutID,parent,false);
            viewHolder.down_bt = (ImageButton) view.findViewById(R.id.down_bt);
            viewHolder.down_singer = (TextView) view.findViewById(R.id.down_singer);
            viewHolder.down_title = (TextView) view.findViewById(R.id.down_title);
            view.setTag(viewHolder);

        }else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }


        String title = resource.get(position).getSongName();
        title = title.replaceAll("<em>","");
        title = title.replaceAll("</em>","");
        viewHolder.down_title.setText(title);

        String singer = resource.get(position).getSingerName();  //去除异常的字符串

        singer = singer.replaceAll("<em>","");
        singer = singer.replaceAll("</em>","");
        viewHolder.down_singer.setText(singer);


        return view;
    }

    class ViewHolder{
        ImageButton down_bt;
        TextView down_title;
        TextView down_singer;
    }
}
