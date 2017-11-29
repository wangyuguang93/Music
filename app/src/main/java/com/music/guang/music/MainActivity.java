package com.music.guang.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.music.guang.music.Adapter.SimpleFragmentPagerAdapter;
import com.music.guang.music.Fragment.BendiMusicFragment;
import com.music.guang.music.Fragment.NetWorkMusicFragment;
import com.music.guang.music.NetworkMusic.GetMusic;
import com.music.guang.music.Service.MusicPlayService;
import com.music.guang.music.Utilt.NetworkUtils;
import com.music.guang.music.Utilt.Timezh;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private SimpleFragmentPagerAdapter pagerAdapter;

    private ViewPager viewPager;
    private BendiMusicFragment bendiMusicFragment;
    private NetWorkMusicFragment netWorkMusicFragment;
    private TabLayout tabLayout;
    private ImageView img_user_head;
    private MusicPlayService musicPlayService;
    private ImageView img_z_last,img_z_play,img_z_next,img_z_touxiang;
    private TextView tv_Song_Title,tv_startTime,tv_endTime,tv_Song_num;
    private SeekBar seekBar;
    private boolean isPlayed=false;//是否播放过
    private ReadMainBroadCastReceiver bmrcMain;
    private SearchView searchView;
    private int numkey=0;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private String ReadServiceMsg="ReadServiceMsg",MainMsg="MainMsg";
    /*当前播放歌曲编号*/
    int ListNum = 0,progress;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        unbindService(sc);
        if (bmrcMain!=null){
            unregisterReceiver(bmrcMain);
        }
        //android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找控件
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            img_z_last = (ImageView) findViewById(R.id.img_z_last);
            img_z_play = (ImageView) findViewById(R.id.img_z_play);
            img_z_next = (ImageView) findViewById(R.id.img_z_next);
            img_z_touxiang = (ImageView) findViewById(R.id.img_z_touxiang);
            tv_Song_Title = (TextView) findViewById(R.id.tv_Song_Title);
            tv_startTime = (TextView) findViewById(R.id.tv_startTime);
            tv_endTime = (TextView) findViewById(R.id.tv_endTime);
            tv_Song_num = (TextView) findViewById(R.id.tv_Song_num);
            seekBar = (SeekBar) findViewById(R.id.seekBar);
            mAdView = (AdView) findViewById(R.id.ad_view);

            //广告
            // MobileAds.initialize(this, "ca-app-pub-3940256099942544/5224354917");

            //     if (NetworkUtils.isNetworkAvailable(this)){
            MobileAds.initialize(this, "ca-app-pub-2160621189322911~4470473187");
            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
         //   mAdView.setAdUnitId(BannerId());
            mAdView.loadAd(adRequest);


            //插页广告
            mInterstitialAd = new InterstitialAd(this);
            //  mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.setAdUnitId(InsertId());
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //}

        //设置监听器
        img_z_last.setOnClickListener(this);
        img_z_play.setOnClickListener(this);
        img_z_next.setOnClickListener(this);
        img_z_touxiang.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity.this.progress=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayService.SeekTo(progress);
            }
        });
        //设置广播接收器
        bmrcMain = new ReadMainBroadCastReceiver();
        IntentFilter filter = new IntentFilter(MainMsg);
        registerReceiver(bmrcMain, filter);
        System.out.println("Main广播接收器被创建....");

        // toolbar.setTitle("");
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayShowHomeEnabled(false);

        DrawerLayout drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
         img_user_head= (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_user_head);
        img_user_head.setOnClickListener(this);
        //连接服务
        Connection();
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        bendiMusicFragment=(BendiMusicFragment) pagerAdapter.getItem(0);
        netWorkMusicFragment=(NetWorkMusicFragment) pagerAdapter.getItem(1);




       // ButterKnife.bind(this);
    }catch (Exception e){
        e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toogle, menu);//指定Toolbar上的视图文件
        MenuItem menuItem = menu.findItem(R.id.ab_search);
        searchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
       final SearchView.SearchAutoComplete searchTextArea = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        // 监听软键盘的删除键
        searchTextArea.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    numkey++;
                    // 在这里加判断的原因是点击一次软键盘的删除键,会触发两次回调事件
                    if (numkey % 2 != 0) {
                        String s = searchTextArea.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            searchTextArea.setText("" + s.substring(0, s.length() - 1));
                            // 将光标移到最后
                            searchTextArea.setSelection(searchTextArea.getText().length());
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (NetworkUtils.isNetworkAvailable(MainActivity.this)&&netWorkMusicFragment.isLoad()){
                    GetMusic.getInstance().SearchMusic(MainActivity.this,netWorkMusicFragment.getMusicList(),query,1);
                    viewPager.setCurrentItem(1);
                    netWorkMusicFragment.setNum(1);
                }else {
                    Toast.makeText(MainActivity.this,R.string.network_fail,Toast.LENGTH_LONG).show();
                };
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
//            case R.id.nav_bendi_music:
//
//                break;
//            case R.id.nav_play_list:
//
//                break;
//            case R.id.nav_download_manage:
//
//                break;
//            case R.id.nav_setting:
//
//                break;
            case R.id.nav_about:
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.about);
                builder.setMessage(R.string.aboutmsg);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                break;
            case R.id.nav_exit:
                if (musicPlayService.isPlaying())
                {
                    musicPlayService.PlayStop();
                }
                finish();
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onClick(View v) {
        if (bendiMusicFragment.getMusicListData().size()==0&&musicPlayService.getType()==0){
            Toast.makeText(this,R.string.no_play_songs,Toast.LENGTH_SHORT).show();
            return;
        }

        int id = v.getId();
        switch (id){
            case R.id.img_user_head:
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.img_z_last:
                musicPlayService.PlayUpper(ListNum);
                break;
            case R.id.img_z_next:
                musicPlayService.PlayNext(ListNum);
                break;
            case R.id.img_z_play:
                musicPlayService.PauseMUS();
        }
    }

    private void Connection() {
        Intent intent = new Intent(MainActivity.this, MusicPlayService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection sc = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            musicPlayService = null;

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            musicPlayService = ((MusicPlayService.MusicBinder) (service)).getService();
            bendiMusicFragment.setMusicPlayService(musicPlayService);
            if (netWorkMusicFragment!=null){
                netWorkMusicFragment.setMusicPlayService(musicPlayService);
            }

            musicPlayService.setMusicListData(MainActivity.this,
                    bendiMusicFragment.getMusicListData(),tv_startTime,
                    seekBar);

        }
    };


    //广播接收器
    private class ReadMainBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("msg")){
                case "ListNum":
                    ListNum=intent.getIntExtra("int",ListNum);
                    bendiMusicFragment.setSelection(ListNum);
                    break;
                case "isPlay":
                    try {
                        if (intent.getIntExtra("int",ListNum)==0){
                            img_z_play.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                        }if (intent.getIntExtra("int",ListNum)==1){
                            isPlayed=true;
                            tv_Song_num.setText(""+(ListNum+1)+"/"+bendiMusicFragment.getMusicListData().size());
                            tv_Song_Title.setText(intent.getStringExtra("obj"));
                            tv_endTime.setText(Timezh.getDisTimeLong(intent.getIntExtra("int2",0)));
                            img_z_play.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                            if (bendiMusicFragment.getMusicListData().get(ListNum).getBnendi_pic()!=null&&musicPlayService.getType()==0){
                                img_z_touxiang.setImageBitmap(bendiMusicFragment.getMusicListData().get(ListNum).getBnendi_pic());
                            }else if(netWorkMusicFragment.getMusicList().get(ListNum).getPic()!=null&&musicPlayService.getType()==1){
                                img_z_touxiang.setImageBitmap(netWorkMusicFragment.getMusicList().get(ListNum).getPic());
                            }else {
                                img_z_touxiang.setImageDrawable(getDrawable(R.drawable.deault_zhuanji_mini));
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                break;
                case "updatemusicdate":
                    bendiMusicFragment.updateMusiclist();
                    break;
                case "deletefile":
                    File file=new File(intent.getStringExtra("fileName"));
                    bendiMusicFragment.delete(file);
                    bendiMusicFragment.deleteupdate();
                    break;
                case "bendiPlay":

                    bendiMusicFragment.play(intent.getIntExtra("int",ListNum));
                    break;
                case "exit":
                    finish();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isPlayed) {
//            if (bmrcMain!=null){
//                unregisterReceiver(bmrcMain);
//            }
            ShowAd();

            finish();
            return true;
        } else {
            ShowAd();
            if (keyCode == KeyEvent.KEYCODE_BACK && isPlayed) {
                moveTaskToBack(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void ShowAd(){
        if (mInterstitialAd!=null&&mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };
    private native String BannerId();
    private native String InsertId();
}
