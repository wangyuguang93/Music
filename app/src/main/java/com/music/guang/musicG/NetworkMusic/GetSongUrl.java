package com.music.guang.musicG.NetworkMusic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.music.guang.musicG.NetworkData;
import com.music.guang.musicG.Utilt.MD5Utils;
import com.music.guang.musicG.Utilt.music_API;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jsb-1 on 2017/11/25.
 */

public class GetSongUrl extends AsyncTask<Void,Void,NetworkData>{
    private String uri;
    private NetworkData info;
    private String urlType;
    private Context context;
    private String ReadServiceMsg = "ReadServiceMsg", MainMsg = "MainMsg";
    public GetSongUrl(Context context,NetworkData networkData){
//    this.uri=uri;
    this.info=networkData;
    this.context=context;
 //   this.urlType=urlType;
    }
    @Override
    protected NetworkData doInBackground(Void... voids) {
        try {

            String hash320=info.getHash320();
            String hashsq=info.getSqhash();
            String key320=MD5Utils.getMD5(hash320+"kgcloud");
            String keysq= MD5Utils.getMD5(hashsq+"kgcloud");
            String addr320 = music_API.KgmusicDownload+"&hash="+hash320+"&key="+key320;
            String addrsp = music_API.KgmusicDownload+"&hash="+hashsq+"&key="+keysq;
            Log.d("addr", addr320);
            URL url320 = new URL(addr320);
            URL urlsq = new URL(addrsp);
            //获取320链接
            InputStream ips320 = url320.openConnection().getInputStream();

            ByteArrayOutputStream outStream320 = new ByteArrayOutputStream();
            byte[] buffer320 = new byte[1024];
            int len320 = 0;
            while( (len320=ips320.read(buffer320)) != -1 ){
                outStream320.write(buffer320, 0, len320);
            }
            ips320.close();
            String jason320 = new String(outStream320.toByteArray());
            JSONObject rootJson320 = new JSONObject(jason320);
            String status320 = rootJson320.getString("status");

            if(status320.equals("1")){
                info.setUrl320(rootJson320.getString("url"));
                info.setExtName(rootJson320.getString("extName"));
            }else{
                //Logger.e(TAG, "getLinks error errorCode=" + errorCode);
            }
            //获取无损链接
            InputStream ipssq = urlsq.openConnection().getInputStream();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffersq = new byte[1024];
            int lensq = 0;
            while( (lensq=ipssq.read(buffersq)) != -1 ){
                outStream.write(buffersq, 0, lensq);
            }
            ipssq.close();
            String jasonsq = new String(outStream.toByteArray());
            JSONObject rootJsonsq = new JSONObject(jasonsq);
            String statussq= rootJsonsq.getString("status");

            if(statussq.equals("1")){
                info.setUrlsq(rootJsonsq.getString("url"));
                info.setExtNamesq(rootJsonsq.getString("extName"));
            }else{
                //Logger.e(TAG, "getLinks error errorCode=" + errorCode);
            }

            hash320=null;
            hashsq=null;
            key320=null;
            keysq=null;
            addr320 = null;
            addrsp = null;
            url320=null;
            urlsq=null;
            ips320=null;
            ipssq=null;
            jasonsq=null;
            jason320=null;
            rootJson320=null;
            rootJsonsq=null;
            status320=null;
            statussq=null;
            buffer320=null;
            buffersq=null;
            return info;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }



        return info;
    }

    @Override
    protected void onPostExecute(NetworkData s) {
        super.onPostExecute(s);
        Intent urlplay = new Intent();
        urlplay.putExtra("msg", "urlok");
        urlplay.setAction(ReadServiceMsg);
        context.sendBroadcast(urlplay);
    }
}
