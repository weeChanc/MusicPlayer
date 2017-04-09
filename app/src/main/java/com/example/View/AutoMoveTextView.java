package com.example.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 铖哥 on 2017/4/9.
 */

public class AutoMoveTextView extends android.support.v7.widget.AppCompatTextView {

    public AutoMoveTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoMoveTextView(Context context) {
        super(context);
    }

    public AutoMoveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean isFocused() {
        return true;
    }
}
