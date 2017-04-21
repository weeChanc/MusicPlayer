package com.example.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MyAdapter.DownLoadListAdapter;
import com.example.mylatouttest.MainActivity;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.example.song.Hash;
import com.example.song.SongGetter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 铖哥 on 2017/4/10.
 */


/**
 * 下载页面的FRAGMENT 支持下拉推荐歌曲 下载歌曲
 */
public class FragDown extends Fragment {

    SwipeRefreshLayout refreshLayout;
    EditText editText;
    Button button;
    ListView listView;
    ArrayList<Hash> hashes;
    DownLoadListAdapter downLoadListAdapter;
    ImageButton back;

    public Handler  handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.setRefreshing(false);
            if(hashes!=null) {
                downLoadListAdapter = new DownLoadListAdapter(getContext(), hashes, R.layout.downitem, new int[]{R.id.item_title, R.id.down_singer, R.id.down_bt});
                LayoutAnimationController controller = new LayoutAnimationController(AnimationUtils.loadAnimation(getContext(),R.anim.downloadanim));
                controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
                listView.setLayoutAnimation(controller);
                listView.setAdapter(downLoadListAdapter);
                Toast.makeText(getContext(), "为您推荐了30首歌曲", Toast.LENGTH_SHORT).show();
            }else
            Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editText.requestFocus();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                      search(); //设置 软键盘回车按键 为 搜索 歌曲

                }
                return true;
            }


        });



        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //自动弹出软键盘

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search();
            }
        });

        refreshLayout.setColorSchemeColors(0xff34a853,0xffea4335,0xfffbbc05,0xff4285f4); //进度条颜色变化
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragdown,container,false);

         editText = (EditText) view.findViewById(R.id.edit);
         button = (Button) view.findViewById(R.id.button2);
         listView = (ListView) view.findViewById(R.id.down_list);
         refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.Refesh);
         back = (ImageButton)view.findViewById(R.id.down_back);

         hashes = new ArrayList<>();

        return view;
    }

    private void search(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hashes = (ArrayList<Hash>) SongGetter.getAllSong(editText.getText().toString());
                    handler.sendEmptyMessage(0);
                }catch (Exception e)
                {
                    e.printStackTrace();            //下拉刷新列表
                }
            }
        }).start();
    }


}
