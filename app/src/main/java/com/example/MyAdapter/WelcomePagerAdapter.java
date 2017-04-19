package com.example.MyAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 铖哥 on 2017/4/17.
 */

public class WelcomePagerAdapter extends PagerAdapter {

    List<View> list;
    Button start;
    Context context;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));

        return list.get(position);
    }

    public WelcomePagerAdapter(List<View> list , final Context context) {
        super();
        this.context = context;
        this.list = list;

        start = (Button) list.get(2).findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.MAINMUSIC");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

                SharedPreferences share = context.getSharedPreferences("data",MODE_PRIVATE);
                share.edit().putInt("MODE", MyApplication.ORDER).apply();
    }
});

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        list.remove(list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;

    }
}
