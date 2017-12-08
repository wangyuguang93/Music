package com.music.guang.musicG.NetworkMusic;

import android.content.Context;
import android.util.Log;

import com.music.guang.musicG.NetworkData;
import com.music.guang.musicG.Utilt.music_API;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by jsb-1 on 2017/11/24.
 */

public class GetMusic {
   private static GetMusic getMusic;
    private String key;
    private int num;
    private  List<NetworkData> musicList;
    public GetMusic(String key,int num){
        this.key=key;
        this.num=num;
    }
    public GetMusic(){
    }
    public static GetMusic getInstance(){
        if (getMusic==null){
            getMusic=new GetMusic("新歌",1);
        }
        return getMusic;
    }
public List<NetworkData> SearchMusic(Context context,List<NetworkData> list, String key, int num) {
    InputStream inStream = null;
    try {
        String mUrl="";
        if (num==1){
            list.clear();
            this.key=key;
            this.num=num;
             mUrl = music_API.Kgmusic + URLEncoder.encode(key, "UTF-8");
        }else {
             mUrl = music_API.Kgmusic + URLEncoder.encode(this.key, "UTF-8");
        }


        Log.d("key", "" + key);

        mUrl+="&page="+num;
        URL url = new URL(mUrl);
        SearchMusicIml searchMusicIml=new SearchMusicIml(context, list,url);
        searchMusicIml.execute();
       // musicList.addAll(list);

        return list;

    } catch (Exception e) {
        //onSearchResult(null);
        e.printStackTrace();
    }
    return musicList;
}
}
