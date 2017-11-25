package com.music.guang.music.NetworkMusic;

import android.os.AsyncTask;
import android.util.Log;

import com.music.guang.music.NetworkData;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jsb-1 on 2017/11/25.
 */

public class GetSongUrl extends AsyncTask<Void,Void,String>{
    private String uri;
    private NetworkData networkData;
    private String urlType;
    public GetSongUrl(String uri, NetworkData networkData,String urlType){
    this.uri=uri;
    this.networkData=networkData;
    this.urlType=urlType;
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(uri);
             InputStream ips = url.openConnection().getInputStream();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=ips.read(buffer)) != -1 ){
                outStream.write(buffer, 0, len);
            }
            ips.close();
            String jason= new String(outStream.toByteArray());
            JSONObject rootJson = new JSONObject(jason);
            String status = rootJson.getString("status");

            if(status.equals("1")){
                if (urlType.equals("320")){
                    networkData.setUrl320(rootJson.getString("url"));
                    networkData.setExtName(rootJson.getString("extName"));
                }else if (urlType.equals("128")){
                    networkData.setUrl128(rootJson.getString("url"));
                    networkData.setExtName(rootJson.getString("extName"));
                }else {
                    networkData.setUrlsq(rootJson.getString("url"));
                    networkData.setExtNamesq(rootJson.getString("extName"));
                }
                return rootJson.getString("url");
            }else {
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
