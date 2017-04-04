package com.example.MyAdapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 铖哥 on 2017/4/4.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> list;
    private ArrayList<String> tab;

    public ViewPagerAdapter(List<View> list, ArrayList<String> tab ) {
        this.list = list;
        this.tab = tab;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tab.get(position);
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
