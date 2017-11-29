package com.music.guang.music.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.music.guang.music.MainActivity;
import com.music.guang.music.MusicData;
import com.music.guang.music.R;

import java.io.File;
import java.util.List;

/**
 * Created by guang on 2017/8/18.
 */

public class BendiMusicAdapter extends BaseAdapter {
   private LayoutInflater mInflater;
   private Context context;
    //所在Fragment
    private ItemClickCallable fragment;
    private List<MusicData> musicListData;
    private int position;

    public BendiMusicAdapter(Context context,List<MusicData> musicListData){
        this.context=context;
        mInflater=LayoutInflater.from(context);
        this.musicListData=musicListData;
    }

    @Override
    public int getCount() {
        return musicListData.size();
    }

    @Override
    public Object getItem(int position) {
        return musicListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.position=position;
        if (convertView== null){
            convertView=mInflater.inflate(R.layout.music_item,null);
            holder=new ViewHolder();
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
            holder.tv_music_Name.setText(musicListData.get(position).getDisName());

            Bitmap pic = musicListData.get(position).getBnendi_pic();
            if (pic != null) {
                holder.img_ico.setImageBitmap(musicListData.get(position).getBnendi_pic());
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
private void showPopupMenu(View view,final int index) {
    // View当前PopupMenu显示的相对View的位置
    PopupMenu popupMenu = new PopupMenu(context, view);
    // menu布局
    view.getId();

    popupMenu.getMenuInflater().inflate(R.menu.bendi_option, popupMenu.getMenu());
    // menu的item点击事件
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()) {
                case R.id.bendi_option_delete:
                    String deleteurl=musicListData.get(index).getUrl();
                    String song=musicListData.get(index).getDisName();
                    //Log.d("删除", ""+index);
                    Log.d("删除", ""+deleteurl);
                    //	Toast.makeText(context, song, Toast.LENGTH_SHORT).show();
                    musicListData.remove(index);
                    Intent intent = new Intent();
                    intent.putExtra("msg", "deletefile");
                    intent.putExtra("fileName",deleteurl);
                    intent.setAction("MainMsg");
                    context.sendBroadcast(intent);
                    MediaScannerConnection.scanFile(context,
                            new String[] { deleteurl },
                            null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);

                                }
                            });
                    break;
                case R.id.bendi_option_play:
                    Intent play = new Intent();
                    play.putExtra("msg", "bendiPlay");
                    play.putExtra("int",index);
                    play.setAction("MainMsg");
                    context.sendBroadcast(play);
                    break;
                case R.id.bendi_option_xiangqing:
                    AlertDialog.Builder xiangqing=new AlertDialog.Builder(context);
                    xiangqing.setTitle("详情");
                    xiangqing.setMessage("文件名："+musicListData.get(index).getDisName()+"\n"+"专辑:"+musicListData.get(index)
                            .getAlbum()+"\n"+"大小:"+musicListData.get(index).getSize()/1024/1024+"M"+"\n"+"位置："+musicListData.get(index).getUrl());
                    xiangqing.create().show();
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
}
