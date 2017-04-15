package com.example.MyAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 铖哥 on 2017/4/1.
 */

public class MySimpleAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> resource;
    private List<Map<String, String>> data;
    private int layoutID;
    private LayoutInflater inflater;
    private MyApplication myApplication = MyApplication.getApplication();
    private Thread lyricThread;
    private Animation spin;


    public MySimpleAdapter(Context context, List<Map<String, String>> resource, int layoutID) {
        this.context = context;
        this.resource = resource;
        this.layoutID = layoutID;
        inflater = LayoutInflater.from(context);
        lyricThread = myApplication.getThread();

        spin = AnimationUtils.loadAnimation(context,R.anim.spin);

        data = myApplication.getData();
//        myApplication.setAdapter(MySimpleAdapter.this);
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
    public View getView(final int position, final View convertView, ViewGroup parent) {

        final View view;
        final ViewHolder viewHolder;

        if (convertView == null) {

            view = inflater.inflate(layoutID, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.title_tv = (TextView) view.findViewById(R.id.item_title);
            viewHolder.singer_tv = (TextView) view.findViewById(R.id.item_singer);
            viewHolder.love_bt = (ImageButton) view.findViewById(R.id.item_love);
            viewHolder.play_bt = (Button) view.findViewById(R.id.item_play);
            viewHolder.card = (CardView) view.findViewById(R.id.card);
            viewHolder.duration = (TextView)view.findViewById(R.id.item_duration);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.singer_tv.setText(resource.get(position).get("singer"));
        viewHolder.title_tv.setText(resource.get(position).get("title"));
        viewHolder.title_tv.setTextColor(Color.BLACK);

        int duration = Integer.parseInt(resource.get(position).get("duration"));
        viewHolder.duration.setText( duration/1000/60 +" : "+ duration%60 +"" );


        addListener(viewHolder);

        viewHolder.play_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // MySimpleAdapter.this.notifyDataSetChanged();
                myApplication.setPosition(position);
                myApplication.setIsPlay(true);
                lyricThread.interrupt();
                Intent intent = new Intent("com.example.MainActivity.STARTMUSIC");
                intent.putExtra("POSITION", true);
                if (resource.get(position).get("title").equals(data.get(position).get("title")))
                    intent.putExtra("LOCATION", position);//本地列表                                      //为本地列表与其他列表做区分
                else
                    intent.putExtra("LOCATION", Integer.parseInt(resource.get(position).get("position")));

                context.sendBroadcast(intent);                              //下面的方法找不到对应的position只能放上来
                                                                                //点击后通知主界面更新图标
                Intent intent2 = new Intent("notification_play_pause");
                intent2.putExtra("LIST", true);
                context.sendBroadcast(intent2);

//                    data.get(position).remove("isplay");
//                    data.get(position).put("isplay","T");
//                    myApplication.setData(data);
//                    MySimpleAdapter.this.notifyDataSetChanged();


            }
        });

        viewHolder.love_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myApplication.getDp();
                Cursor cursor = db.query("Like", null, null, null, null, null, null, null);
                ContentValues values = new ContentValues();
                values.put("title", resource.get(position).get("title"));
                values.put("singer", resource.get(position).get("singer"));
                values.put("duration", resource.get(position).get("duration"));
                values.put("position", position);

                boolean common = false;
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getString(cursor.getColumnIndex("title")).equals(values.get("title")) &&
                                cursor.getString(cursor.getColumnIndex("duration")).equals(values.get("duration"))) {
                            common = true;
                        }
                    } while (cursor.moveToNext());
                }
                if (!common)
                    db.insert("Like", null, values);
                cursor.close();
            }

        });

        viewHolder.play_bt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                View rootView = LayoutInflater.from(context).inflate(R.layout.musicplayer_main, null);
                View contentView = LayoutInflater.from(context).inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(contentView, 400, WindowManager.LayoutParams.WRAP_CONTENT, true);

                popupWindow.setAnimationStyle(R.style.popup);
                popupWindow.showAsDropDown(viewHolder.love_bt, -360, -20);
                Button ensure = (Button) contentView.findViewById(R.id.ensure);
                Button quit = (Button) contentView.findViewById(R.id.quit);

                ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SQLiteDatabase db = myApplication.getDp();
                        db.delete("Like", "title=?", new String[]{resource.get(position).get("title")});
                        db.delete("Recent", "title=?", new String[]{resource.get(position).get("title")});

                        resource.remove(position);
                        MySimpleAdapter.this.notifyDataSetChanged();

                        popupWindow.dismiss();


                    }
                });

                quit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                return true;
            }
        });

        return view;
    }


    private class ViewHolder {
        TextView title_tv;
        TextView singer_tv;
        TextView duration;
        ImageButton love_bt;
        Button play_bt;

        CardView card;
    }

    private void addListener(ViewHolder viewHolder) {
        viewHolder.love_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myApplication.getDp();
                Cursor cursor = db.query("Like", null, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    do {
                        Log.e("message", cursor.getString(cursor.getColumnIndex("title")));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }


        });


    }


}
