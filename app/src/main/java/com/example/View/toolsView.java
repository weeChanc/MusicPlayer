package com.example.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mylatouttest.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 铖哥 on 2017/4/9.
 */

public class toolsView extends LinearLayout {

      String mode = "orl";
      ImageButton mode_bt;
      Context context;
    public toolsView(final Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        this.context = context;

        View view  = LayoutInflater.from(context).inflate(R.layout.tools,this);

        mode_bt = (ImageButton) view.findViewById(R.id.mode_bt);
        mode_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode();

                SharedPreferences.Editor editor = context.getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("MODE", mode);
                editor.apply();
            }
        });

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);
            mode = sharedPreferences.getString("MODE", "rlo");
            if (mode.charAt(2) == 'o') mode_bt.setImageResource((R.drawable.orderplay));
            if (mode.charAt(2) == 'r') mode_bt.setImageResource((R.drawable.randomblue));
            if (mode.charAt(2) == 'l') mode_bt.setImageResource((R.drawable.loopplaybule));

            Intent modeintent = new Intent("com.example.LocalMusic.MODE");
            modeintent.putExtra("MODE", mode.charAt(2));
            context.sendBroadcast(modeintent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("info", "didnt' find");
        }

    }

    void setMode() {
        Intent modeintent = new Intent("com.example.LocalMusic.MODE");
        if (mode.charAt(0) == 'o') {
            mode_bt.setImageResource(R.drawable.orderplay);
            modeintent.putExtra("MODE", 'o');
            context.sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'o';
        } else if (mode.charAt(0) == 'r') {
            mode_bt.setImageResource((R.drawable.randomblue));
            modeintent.putExtra("MODE", 'r');
            context.sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'r';
        } else if (mode.charAt(0) == 'l') {
            mode_bt.setImageResource((R.drawable.loopplaybule));
            modeintent.putExtra("MODE", 'l');
            context.sendBroadcast(modeintent);
            mode = mode.substring(1);
            mode = mode + 'l';
        }
    }
}
