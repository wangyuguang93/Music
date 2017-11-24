package com.music.guang.music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.music.guang.music.R;

/**
 * Created by guang on 2017/8/18.
 */

public class NetWorkMusicApapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context context;
    TextView SongName;

    public NetWorkMusicApapter(Context context){
            this.context=context;
            mInflater=LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView== null){
            convertView=mInflater.inflate(R.layout.music_item,null);
            SongName= (TextView) convertView.findViewById(R.id.music_item_SongName);
            SongName.setText("网络音乐");

        }
        return convertView;
    }
}
