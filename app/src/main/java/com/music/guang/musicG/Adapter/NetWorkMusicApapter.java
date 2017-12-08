package com.music.guang.musicG.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.music.guang.musicG.NetworkData;
import com.music.guang.musicG.NetworkMusic.Net_music_download;
import com.music.guang.musicG.R;
import com.music.guang.musicG.Service.MusicPlayService;
import com.music.guang.musicG.Utilt.NetworkUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guang on 2017/8/18.
 */

public class NetWorkMusicApapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context context;
    TextView SongName;
    private int position;
    private String durl[];
    private ItemClickCallable fragment;
    private List<NetworkData> musicList;
    private String url;
    private MusicPlayService musicPlayService;
    private String ReadServiceMsg="ReadServiceMsg",MainMsg="MainMsg";

    public NetWorkMusicApapter(Context context,List<NetworkData> musicList){
            this.context=context;
            this.musicList=musicList;
            mInflater=LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return musicList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.position=position;
        if (convertView== null){
            convertView=mInflater.inflate(R.layout.music_item,null);
            holder= new ViewHolder();
            holder.tv_music_num=(TextView)convertView.findViewById(R.id.music_item_num);
            holder.tv_music_Name=(TextView) convertView.findViewById(R.id.music_item_SongName);

            holder.img_ico=(ImageView) convertView.findViewById(R.id.music_item_ico);
            holder.img_more=(ImageView) convertView.findViewById(R.id.music_item_more);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img_more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showPopupMenu(v,position);
            }
        });
        if (getCount() != 0) {


            holder.tv_music_num.setText("" + (position + 1) + ".");
            holder.tv_music_Name.setText(musicList.get(position).getFilename());

            Bitmap pic = musicList.get(position).getPic();
            if (pic != null) {
                holder.img_ico.setImageBitmap(pic);
            } else {
                holder.img_ico.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lab));
            }
        }
//			System.out.println(almid[position]);
//		}

        convertView.setTag(holder);
        return convertView;
    }


    static class ViewHolder {
        private TextView tv_music_num;
        private ImageView img_ico;
        private TextView tv_music_Name;
        private ImageView img_more;

    }

//    private void showPopupMenu(View view,final int index) {
//        // View当前PopupMenu显示的相对View的位置
//        PopupMenu popupMenu = new PopupMenu(context, view);
//        // menu布局
//        view.getId();
//
//        popupMenu.getMenuInflater().inflate(R.menu.bendi_option, popupMenu.getMenu());
//        // menu的item点击事件
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                // Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
//                switch (item.getItemId()) {
//                    case R.id.bendi_option_delete:
//                        break;
//                    case R.id.bendi_option_play:;
//                        break;
//                    case R.id.bendi_option_xiangqing:
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
//        // PopupMenu关闭事件
//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//                // Toast.makeText(mContext, "关闭PopupMenu", Toast.LENGTH_SHORT).show();
//            }
//        });
//        popupMenu.show();
//    }
private void showPopupMenu(View view,final int index) {
    // View当前PopupMenu显示的相对View的位置
    PopupMenu popupMenu = new PopupMenu(context, view);
    // menu布局
    view.getId();

    popupMenu.getMenuInflater().inflate(R.menu.option, popupMenu.getMenu());
    // menu的item点击事件
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()) {
                case R.id.option_download:
                    List<String> list=new ArrayList<String>();
                    List<String> downloadurl=new ArrayList<String>();
                    String url128=musicList.get(index).getUrl128();
                    String url320=musicList.get(index).getUrl320();
                    String urlsq=musicList.get(index).getUrlsq();
                    String urlmv=musicList.get(index).getUrlmv();

                    if (url320!=null) {

                        float size=Float.parseFloat(musicList.get(index).getFilesize320());
                        DecimalFormat fnum   =   new   DecimalFormat("##0.00");
                        size=++size/1024/1024;
                        String   dd=fnum.format(size);
                        list.add("高清音质"+"("+dd+"M)");
                        downloadurl.add(url320);
                    }
                    if (url128!=null) {
                        float size=Float.parseFloat(musicList.get(index).getFilesize128());
                        DecimalFormat   fnum   =   new   DecimalFormat("##0.00");
                        size=++size/1024/1024;
                        String   dd=fnum.format(size);
                        list.add("标准音质"+"("+dd+"M)");
                        downloadurl.add(url128);
                    }
                    if (urlsq!=null) {
                        float size=Float.parseFloat(musicList.get(index).getSqfilesize());
                        DecimalFormat   fnum   =   new   DecimalFormat("##0.00");
                        size=++size/1024/1024;
                        String   dd=fnum.format(size);
                        list.add("无损音质"+"("+dd+"M)");
                        downloadurl.add(urlsq);
                    }
                    if (urlmv!=null) {
                        float size=Float.parseFloat(musicList.get(index).getSqfilesize());
                        DecimalFormat   fnum   =   new   DecimalFormat("##0.00");
                        size=++size/1024/1024;
                        String   dd=fnum.format(size);
                        list.add("MV"+"("+dd+"M)");
                        downloadurl.add(urlmv);
                    }
                    final String dl[]=list.toArray(new String[list.size()]);
                    durl=downloadurl.toArray(new String[list.size()]);
                    AlertDialog.Builder download=new AlertDialog.Builder(context);
                    download.setTitle("请选择");
                    download.setSingleChoiceItems(dl, 0, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            int d=which;
                            if (d==-1) {
                                d=0;
                            }
                            url=durl[d];
                        }
                    });

                    download.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            try {
                                String songname=musicList.get(index).getFilename();
                                String extname=musicList.get(index).getExtName();
                                if (url==null) {
                                    url=durl[0];
                                }
                                System.out.print(musicList.get(index).getFilesize320());
                                //自定义下载
                                Net_music_download net_music_download=new Net_music_download(context);
                                net_music_download.execute(url,songname,extname);
                                //使用系统下载管理
//							MydownloadManager manager=new MydownloadManager(mContext, url, songname);
//							manager.downloadmusic();

                                Toast.makeText(context, "开始下载:"+songname, Toast.LENGTH_SHORT).show();
                                url=null;
                            } catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }

                        }
                    });
                    download.create().show();
                    //Toast.makeText(mContext, arr[index].getSongname(), Toast.LENGTH_SHORT).show();

                    break;
                case R.id.option_xiangqing:
                    try {
                        DecimalFormat   fnum   =   new   DecimalFormat("##0.00");
                        float size=Float.parseFloat(musicList.get(index).getFilesize320());
                        size=++size/1024/1024;
                        String   dd=fnum.format(size);
                        AlertDialog.Builder netxiangqing=new AlertDialog.Builder(context);
                        netxiangqing.setTitle("详情");
                        netxiangqing.setMessage("文件名："+musicList.get(index).getFilename()+"\n"+"专辑:"+musicList.get(index).getAlbum_name()+"\n"+"大小:"+dd+"M");
                        netxiangqing.create().show();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                    break;
                case R.id.option_play:
                    musicPlayService.setNetworListData(musicList);
                    if (NetworkUtils.isNetworkAvailable(context)){
                        if (musicList.get(index).getPic()==null){
                            Toast.makeText(context,"this song not play",Toast.LENGTH_SHORT).show();
                        }else {
                            musicPlayService.Play(index,1);
                        }


                    }else {
                        Toast.makeText(context,"network fail",Toast.LENGTH_LONG).show();
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });
    // PopupMenu关闭事件
    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
        @Override
        public void onDismiss(PopupMenu menu) {
            // Toast.makeText(mContext, "关闭PopupMenu", Toast.LENGTH_SHORT).show();
        }
    });
    popupMenu.show();
}
    ///////////////////////抽取接口//////////////////>
    /**
     * 点击子项目，通知外部进行处理。
     */
    public interface ItemClickCallable
    {

        /**
         * 点击条目。
         * @param position
         */
        public void intoItem(int position);
    }
    ///////////////////////抽取接口//////////////////<

    public void setMusicPlayService (MusicPlayService musicPlayService){
        this.musicPlayService=musicPlayService;
    }
}
