package com.music.guang.musicG.NetworkMusic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.music.guang.musicG.NetworkData;
import com.music.guang.musicG.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jsb-1 on 2017/11/24.
 */

public class SearchMusicIml extends AsyncTask<String,NetworkData,List<NetworkData>>{
    private List<NetworkData> musicList;
    private NetworkData info;
    private URL url;
    private Context context;
    public SearchMusicIml(Context context,List<NetworkData> musicList,URL url){
        this.musicList=musicList;
        this.url=url;
        this.context=context;
    }
    @Override
    protected List<NetworkData> doInBackground(String... strings) {
        InputStream inStream = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            inStream = conn.getInputStream();

            //解析数据
            ByteArrayOutputStream outkg = new ByteArrayOutputStream();
            byte[] kgbuffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(kgbuffer)) != -1) {
                outkg.write(kgbuffer, 0, len);
            }
            String kgjason = new String(outkg.toByteArray());
            JSONObject kgrootJson = new JSONObject(kgjason);
            String total = kgrootJson.getJSONObject("data").getString("total");
            if (!total.equals("0")) {
                JSONObject data = kgrootJson.getJSONObject("data");
                JSONArray netinfo = data.getJSONArray("info");
                for (int i = 0; i < netinfo.length(); i++) {
                    JSONObject kgitem = netinfo.getJSONObject(i);
                    info=new NetworkData();
                    info.setFilename(kgitem.getString("filename"));
                    info.setSongname(kgitem.getString("songname"));
                    info.setM4afilesize(kgitem.getString("m4afilesize"));
                    info.setHash320(kgitem.getString("320hash"));
                    info.setMvhash(kgitem.getString("mvhash"));
                    info.setFilesize128(kgitem.getString("filesize"));
                    info.setOwnercount(kgitem.getString("ownercount"));
                    info.setSqhash(kgitem.getString("sqhash"));
                    info.setFilesize320(kgitem.getString("320filesize"));
                    info.setDuration(kgitem.getString("duration"));
                    info.setAlbum_id(kgitem.getString("album_id"));
                    info.setHash(kgitem.getString("hash"));
                    info.setSingername(kgitem.getString("singername"));
                    info.setSqfilesize(kgitem.getString("sqfilesize"));
                    info.setAlbum_name(kgitem.getString("album_name"));


                    musicList.add(info);
                    info = null;
                }
            } else {
                Log.d("找不到没有歌曲", "找不到没有歌曲");
            }
            inStream.close();
            return musicList;
        } catch (Exception e) {
            //onSearchResult(null);
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<NetworkData> networkData) {
        if (networkData.size()==0){
            Toast.makeText(context, R.string.no_songs,Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent();
        intent.putExtra("msg", "searchok");
        intent.setAction("networkMsg");
        context.sendBroadcast(intent);
        for (int i=0;i<musicList.size();i++){
            GetImgPic getImgPic=new GetImgPic(context,musicList,i);
            getImgPic.execute();
            musicList.get(i).setisUrlok(true);
            Log.d("获取歌曲链接",musicList.get(i).getFilename());
            GetSongUrl getSongUrl=new GetSongUrl(context,musicList.get(i));
            getSongUrl.execute();
        }

    }
}
