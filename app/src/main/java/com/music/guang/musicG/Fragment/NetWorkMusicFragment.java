package com.music.guang.musicG.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.music.guang.musicG.Adapter.NetWorkMusicApapter;
import com.music.guang.musicG.NetworkData;
import com.music.guang.musicG.NetworkMusic.GetMusic;
import com.music.guang.musicG.R;
import com.music.guang.musicG.Service.MusicPlayService;
import com.music.guang.musicG.Utilt.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guang on 2017/8/18.
 */

public  class NetWorkMusicFragment extends android.support.v4.app.Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private Context context;
    private NetWorkMusicApapter netWorkMusicApapter;
    private static NetWorkMusicFragment pageFragment;
   private ReadNetworkBroadCastReceiver bmrcNetwork;
    private List<NetworkData> musicList=new ArrayList<NetworkData>();
    private int lastItem;
    private   ListView listView=null;
    private int num=1;//
    private boolean isLoad=true;
    private String key="akb48";
    private TextView tv_error;
    private ProgressBar progressBar;
    private MusicPlayService musicPlayService;


        public static NetWorkMusicFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        if (pageFragment==null){
            pageFragment = new  NetWorkMusicFragment();
            //注册广播监听器

        }

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        context=getActivity();
        //注册广播监听器
        //设置广播接收器
        bmrcNetwork = new ReadNetworkBroadCastReceiver();
        IntentFilter filter = new IntentFilter("networkMsg");
        context.registerReceiver(bmrcNetwork, filter);
        System.out.println("Network广播接收器被创建....");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view;
            view = inflater.inflate(R.layout.networkmusic, container, false);

            Log.d("加载onCreateView", "加载onCreateView");
            listView = (ListView) view.findViewById(R.id.network_listview);
            tv_error= (TextView) view.findViewById(R.id.tv_error);
            progressBar= (ProgressBar) view.findViewById(R.id.progressBar);
            tv_error.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            //listView.setText("Fragment #" + mPage)
        if (netWorkMusicApapter == null) {
            netWorkMusicApapter = new NetWorkMusicApapter(context, musicList);
        }
            listView.setAdapter(netWorkMusicApapter);
            if (musicList.size() <= 0) {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    GetMusic.getInstance().SearchMusic(context, musicList, key, num);
                    tv_error.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                else {
                    tv_error.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);

                }
            }
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    if (lastItem == listView.getCount() - 1 && i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                        if (isLoad && num <= 5 && musicList.size() % 20 == 0&&NetworkUtils.isNetworkAvailable(context)) {
                            if (num++ <= 5) {
                                Log.d("加载更多", key + ":" + num);
                                GetMusic.getInstance().SearchMusic(context, musicList, key, num);
                                isLoad = false;
                            }

                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    lastItem = i + i1 - 1;
                }
            });


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    musicPlayService.setNetworListData(musicList);
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        if (musicList.get(i).getPic() == null) {
                            Toast.makeText(context, "this song not play", Toast.LENGTH_SHORT).show();
                        } else {
                            musicPlayService.Play(i, 1);
                        }


                    } else {
                        Toast.makeText(context, R.string.network_fail, Toast.LENGTH_LONG).show();
                    }

                }
            });
            tv_error.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        GetMusic.getInstance().SearchMusic(context, musicList, key, num);
                        tv_error.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(context,R.string.network_fail,Toast.LENGTH_SHORT).show();
                    }
                }
            });

        return view;
    }

    //广播接收器
    private class ReadNetworkBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("msg")){
                case "searchok":
                    tv_error.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    netWorkMusicApapter.notifyDataSetChanged();
                   // isLoad=true;
                    break;
                case "picok":
                    try {
                        netWorkMusicApapter.notifyDataSetChanged();
                        isLoad=true;

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                default:
                    break;
            }
        }
    }
    public List<NetworkData> getMusicList(){
        return musicList;
    }
    public void setNum(int num){
        this.num=num;
    }
    public void setMusicPlayService (MusicPlayService musicPlayService){
        this.musicPlayService=musicPlayService;
        if (netWorkMusicApapter!=null) {
            netWorkMusicApapter.setMusicPlayService(musicPlayService);
        }
    }
    public void setSelection(int position)
    {
        listView.setSelection(position);
    }
    @Override
    public void onDestroy() {
        context.unregisterReceiver(bmrcNetwork);
        super.onDestroy();
    }
    public boolean isLoad()
    {
        return this.isLoad;
    }
}
