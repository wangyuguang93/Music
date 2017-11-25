package com.music.guang.music.NetworkMusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Network;
import android.os.AsyncTask;
import android.util.Log;

import com.music.guang.music.NetworkData;
import com.music.guang.music.Utilt.music_API;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Created by jsb-1 on 2017/11/25.
 */

public class GetImgPic extends AsyncTask<Void,NetworkData,NetworkData> {
private List<NetworkData> list;
private int i;
private Context context;
    public GetImgPic(Context context,List<NetworkData> list,int i){
        this.context=context;
        this.list=list;
        this.i=i;
    }

    @Override
    protected NetworkData doInBackground(Void... voids) {
        try{
            String addr = music_API.KgmusicInfo+list.get(i).getHash();
             Log.d("I", ""+i+"="+addr);
            URL url = new URL(addr);
            if (url!=null){
                InputStream kgmusicinfo = url.openConnection().getInputStream();

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while( (len=kgmusicinfo.read(buffer)) != -1 ){
                    outStream.write(buffer, 0, len);
                }
                kgmusicinfo.close();

                String jason = new String(outStream.toByteArray());
                JSONObject rootJson = new JSONObject(jason);
                String status = rootJson.getString("status");
                if(status.equals("1")){
                    list.get(i).setImgUrl(rootJson.getString("imgUrl").replace("{size}", "200"));
                    list.get(i).setUrl128(rootJson.getString("url"));
                    list.get(i).setReq_hash(rootJson.getString("req_hash"));
                    list.get(i).setAlbum_img(rootJson.getString("album_img").replace("{size}", "150"));


                }else{
                    //Logger.e(TAG, "getLinks error errorCode=" + errorCode);
                }
                if (list.get(i).getImgUrl()!=null&&list.get(i).getPic()==null){
                    for (int j=0;j<list.size()&&j!=i;j++) {
                        if (list.get(i).getSingername().equals(list.get(j).getSingername())){
                            if (list.get(j).getPic()!=null){
                                list.get(i).setPic(list.get(j).getPic());
                                Log.d("pic:","歌手相同的pic已存在，不在下载");
                                return list.get(i);
                            }
                        }
                    }
                    String img=list.get(i).getImgUrl();
                    if (img!=null&&!img.equals("")){
                        URL imgurl=new URL(img);
                        HttpURLConnection httpURLConnection= (HttpURLConnection) imgurl.openConnection();
                        httpURLConnection.setConnectTimeout(5000);// 设置网络连接超时的时间为3秒
                        httpURLConnection.setRequestMethod("GET");
                        int responseCode = httpURLConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) { // 正常连接
                            InputStream   inputStream = httpURLConnection.getInputStream();
                            byte[] imagebytes = null;
                            imagebytes = StreamTool.getBytes(inputStream);
                            list.get(i).setPic(BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length));

                        }
                    }
                }
//                if ( list.get(i).getPic()==null){
//                    list.remove(i);
//                    Intent intent = new Intent();
//                    intent.putExtra("msg", "searchok");
//                    intent.setAction("networkMsg");
//                    context.sendBroadcast(intent);
//                }
                return list.get(i);
            }

        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return list.get(i);
    }

    @Override
    protected void onPostExecute(NetworkData networkData) {
        super.onPostExecute(networkData);
        Intent intent = new Intent();
        intent.putExtra("msg", "searchok");
        intent.setAction("networkMsg");
        context.sendBroadcast(intent);
    }
    public static class StreamTool {
        public static byte[] getBytes(InputStream is) throws Exception {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            is.close();
            bos.flush();
            byte[] result = bos.toByteArray();
            // System.out.println(new String(result));

            return result;
        }
    }

}
