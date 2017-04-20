package com.example.mylatouttest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.example.MusicService.MusicService;
import com.example.MyAdapter.ViewPagerAdapter;
import com.example.VolumechangeReceiver.VolumnChangeReceiver;
import com.example.fragment.FragDown;
import com.example.fragment.FragLocal;
import com.example.fragment.FragMain;
import com.example.fragment.FragLike;
import com.example.fragment.FragRecent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;



public class MainActivity extends AppCompatActivity{

    private MyApplication myApplication; //全局变量

    FragmentManager fm = getSupportFragmentManager();

    private VolumnChangeReceiver volumnChangeReceiver;
    private MusicService musicService;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MBind) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("info", "service disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myApplication = (MyApplication) getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplayer_main);

        myApplication.setActivity(this);



        readytoplay(); //绑服务 注册广播( //音量变化广播//耳机插拔广播)

        LayoutInflater layoutInflater = LayoutInflater.from(this);              //底部播放栏 用ViewPager实现 可以左右滑动 右边显示歌词 左边为底部播放器
        View bottomPlayer = layoutInflater.inflate(R.layout.bottomplayer,null); //填充底部播放栏View
        View lyric = layoutInflater.inflate(R.layout.lyric,null);               //填充歌词显示的View
        ArrayList<View> views = new ArrayList<>();
        views.add(bottomPlayer);
        views.add(lyric);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,this);            //构建适配器
        viewPager.setAdapter(adapter);                                          //给ViewPager设置适配器

        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frag_container,new FragMain());
        ft.commit();

        //启动主界面的Fragment


            Intent intent = new Intent("com.example.MainActivity.REQUSETRES");
            sendBroadcast(intent);


    }


    @Override
    protected void onDestroy() {
//        manager.removeView(bottomPlayer);
        Log.e("info", "MainAcitivit Destory");

        unregisterReceiver(volumnChangeReceiver);

        super.onDestroy();
    }

    private void registerMyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION"); //音量变化广播
        intentFilter.addAction("android.intent.action.HEADSET_PLUG"); //耳机插拔广播
        volumnChangeReceiver = new VolumnChangeReceiver();
        registerReceiver(volumnChangeReceiver, intentFilter); //注册广播
    }

    private void insertDesign() {

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);                //隐藏状态栏
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    long start = 0 ;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - start > 2000 && fm.getBackStackEntryCount() == 0){
            start = System.currentTimeMillis();
            Toast.makeText(this,"再按一次退出", Toast.LENGTH_SHORT).show();
        }else {                                                                 //设置按两次退出程序
            super.onBackPressed();
        }
    }

    private void readytoplay() {

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        startService(intent);
        insertDesign();                 //隐藏状态栏
        registerMyReceiver();

    }

    public void fragRecent(){                                                               //启动最近播放列表
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.up_in,R.anim.down_out,R.anim.up_in,R.anim.up_out);    //动画设置
        ft.replace(R.id.frag_container,new FragRecent());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void fragLike(){                                                                 //启动我喜欢的列表
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.up_in,R.anim.down_out,R.anim.up_in,R.anim.up_out);    //动画设置
        ft.replace(R.id.frag_container,new FragLike());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void fragLocal(){                                                                     //启动本地音乐列表
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.right_in,R.anim.left_out,R.anim.left_in,R.anim.right_out);//动画设置
        ft.replace(R.id.frag_container,new FragLocal());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void fragDown(){                                                                      //启动下载列表
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.right_in,R.anim.left_out,R.anim.left_in,R.anim.right_out); //动画设置
        ft.replace(R.id.frag_container,new FragDown());
        ft.addToBackStack(null);
        ft.commit();
    }

}
