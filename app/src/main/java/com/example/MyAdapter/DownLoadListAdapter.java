package com.example.MyAdapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.song.Hash;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by 铖哥 on 2017/4/10.
 */

public class DownLoadListAdapter extends ArrayAdapter<Hash> {
    int layoutId;


    public DownLoadListAdapter(@NonNull Context context, @LayoutRes int resource, List<Hash> object) {
        super(context, resource,object);
        layoutId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View view = LayoutInflater.from(getContext()).inflate(layoutId,parent,false);
        Hash hash = getItem(position);


        return view;
    }
}
