package com.music.guang.music.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.music.guang.music.Adapter.NetWorkMusicApapter;
import com.music.guang.music.R;

/**
 * Created by guang on 2017/8/18.
 */

public class NetWorkMusicFragment extends android.support.v4.app.Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private Context context;
    private NetWorkMusicApapter netWorkMusicApapter;
    private static NetWorkMusicFragment pageFragment;

    public static NetWorkMusicFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        if (pageFragment==null){
            pageFragment = new  NetWorkMusicFragment();
        }

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        context=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.networkmusic, container, false);
        ListView listView= (ListView) view;
        //listView.setText("Fragment #" + mPage)
        if (netWorkMusicApapter==null)
        {
            netWorkMusicApapter=new NetWorkMusicApapter(context);
        }
        listView.setAdapter(netWorkMusicApapter);
        return view;
    }
}
