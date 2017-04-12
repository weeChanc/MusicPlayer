package com.example.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MyAdapter.DownLoadListAdapter;
import com.example.mylatouttest.MyApplication;
import com.example.mylatouttest.R;
import com.example.song.Hash;
import com.example.song.SongGetter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 铖哥 on 2017/4/10.
 */

public class FragDown extends Fragment {


    EditText editText;
    Button button;
    ListView listView;
    ArrayList<Hash> hashes;
    List<String> titles;
    MyApplication myApplication = MyApplication.getApplication();
    DownLoadListAdapter downLoadListAdapter;
    ArrayAdapter<String> adapter;

    public Handler  handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            downLoadListAdapter = new DownLoadListAdapter(getContext(),hashes,R.layout.downitem,new int[]{R.id.down_title,R.id.down_singer,R.id.down_bt});
            listView.setAdapter(downLoadListAdapter);
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {          //设置 软键盘回车 搜索
                            try {

                                Log.e("tag",v.getText().toString());
                                titles.clear();
                                hashes = (ArrayList<Hash>) SongGetter.getAllSong(v.getText().toString());

                                for (int i = 0; i < hashes.size(); i++) {
                                    titles.add(hashes.get(i).getFileName());

                                }
                                handler.sendEmptyMessage(0);
                            }catch(Exception e) {
                                Intent intent = new Intent("TOAST");
                                intent.putExtra("FAILESEARCH",true);
                                getContext().sendBroadcast(intent);
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                return true;
            }


        });

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragdown,container,false);

         editText = (EditText) view.findViewById(R.id.edit);
         button = (Button) view.findViewById(R.id.button2);
         listView = (ListView) view.findViewById(R.id.down_list);
         hashes = new ArrayList<>();
         titles = new ArrayList<>();
         titles = new ArrayList<>();



        return view;
    }


}
