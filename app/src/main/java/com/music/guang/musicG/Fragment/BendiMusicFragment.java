package com.music.guang.musicG.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.music.guang.musicG.Adapter.BendiMusicAdapter;
import com.music.guang.musicG.MusicData;
import com.music.guang.musicG.R;
import com.music.guang.musicG.Service.MusicPlayService;
import com.music.guang.musicG.Utilt.MediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BendiMusicFragment extends Fragment{

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private Context context;
    private BendiMusicAdapter bendiMusicAdapter;
    private Cursor cursor;
    private List<MusicData> musicListData = new ArrayList<MusicData>();
    private MusicData musicData;
    private Handler mHandler;
    private View view;
    /*当前播放歌曲编号*/
    int ListNum = 0;
    private ListView listView;
    private static  BendiMusicFragment pageFragment;
    private MusicPlayService musicPlayService;



    public static BendiMusicFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        if (pageFragment==null) {
            pageFragment = new BendiMusicFragment();
        }
        pageFragment.setArguments(args);
        return pageFragment;
    }

    public void delete(File paramFile)
    {
        if (paramFile.isFile()) {
            paramFile.delete();
        }
    }

    public void deleteupdate()
    {
        if (this.bendiMusicAdapter != null) {
            this.bendiMusicAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        context=getActivity();
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        if (bendiMusicAdapter!=null&&listView!=null)
                        bendiMusicAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }

            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bendimusic, container, false);
        listView = (ListView) view;
        //textView.setText("Fragment #" + mPage)
       // String [] str={"aaa","bbb"};
       // if (bendiMusicAdapter==null) {
            bendiMusicAdapter = new BendiMusicAdapter(getActivity(), musicListData);
        //}

        listView.setAdapter(bendiMusicAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListNum=position;
                try {
//                    if (musicPlayService==null)
//                        Log.d("musicPlayService","musicPlayService=null");
                    musicPlayService.Play(ListNum,0);
                }catch (Exception e){
                    //musicPlayService.setMusicListData(musicListData);
                    e.printStackTrace();
                }
            }
        });

        if (musicListData.size()==0) {
//            Thread tscanMusic = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                 //   ScannerMusic();
//                }
//            });
           // tscanMusic.start();
            ScanMusic scanMusic=new ScanMusic();
            scanMusic.execute();
        }


        return view;

    }
    public void play(int paramInt)
    {
        this.ListNum = paramInt;
        try
        {
            this.musicPlayService.Play(this.ListNum, 0);
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }


    // public void ScannerMusic()
    protected class ScanMusic extends AsyncTask<String, MusicData,List<MusicData>>{


        @Override
        protected List<MusicData> doInBackground(String... params) {
            //ScannerMusic();

            {
                try {

                // 查询媒体数据库
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                // 遍历媒体数据库
                Log.d("music disName=", "" + cursor.getCount());// 打印出歌曲名
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        musicData = new MusicData();
                        // 歌曲编号
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                        // 歌曲id
                        int trackId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                        // 歌曲标题
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                        // 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                        // 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        // 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                        String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        // 歌曲的总播放时长：MediaStore.Audio.Media.DURATION
                        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        // 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                        // 歌曲文件显示名字
                        String disName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));

                        // Log.e("music disName=", ""+cursor.getCount());//打印出歌曲名字
                        musicData.setAlbumId(id);
                        musicData.setAlbum(album);
                        musicData.setArtist(artist);
                        musicData.setDisName(disName);
                        musicData.setSize(size);
                        musicData.setTitle(title);
                        musicData.setUrl(url);
                        musicData.setDuration(duration);
                        musicData.setTrackId(trackId);
                        musicData.setBnendi_pic(MediaUtil.getArtwork(context, id, albumId, false, true));

                        publishProgress(musicData);
                        musicData = null;
                        // Log.e("music disName=", ""+test1[j]);//打印出歌曲名字
                        //   mHandler.sendEmptyMessage(0);

                        cursor.moveToNext();
                    }

                    cursor.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            }
            return musicListData;
        }

        @Override
        protected void onProgressUpdate( MusicData... values) {
            super.onProgressUpdate(values);
                try {
                    musicListData.add(values[0]);
                    listView.setVisibility(View.GONE);
                    bendiMusicAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

        }



        @Override
        protected void onPostExecute(List<MusicData> musicDatas) {
            super.onPostExecute(musicDatas);

            try {
                listView.setVisibility(View.GONE);
                bendiMusicAdapter.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public List<MusicData> getMusicListData(){
        return musicListData;
    }
    public void setMusicPlayService (MusicPlayService musicPlayService){
        this.musicPlayService=musicPlayService;
//        if (this.musicPlayService==null)
//                Log.e("警告","musicPlayService==null");
    }
    public void setSelection(int position)
    {
        listView.setSelection(position);
    }
    public void updateMusiclist()
    {
        this.musicListData.clear();
        new ScanMusic().execute(new String[0]);
        this.bendiMusicAdapter.notifyDataSetChanged();
    }

}