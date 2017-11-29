package com.music.guang.music.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.music.guang.music.MainActivity;
import com.music.guang.music.MusicData;
import com.music.guang.music.NetworkData;
import com.music.guang.music.NetworkMusic.GetSongUrl;
import com.music.guang.music.R;
import com.music.guang.music.Utilt.MD5Utils;
import com.music.guang.music.Utilt.NetworkUtils;
import com.music.guang.music.Utilt.Timezh;
import com.music.guang.music.Utilt.music_API;

import java.io.IOException;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

/**
 * Created by guang on 2017/8/19.
 */

public class MusicPlayService extends Service implements  OnCompletionListener, OnPreparedListener, OnBufferingUpdateListener {

    /*播放状态：未播放、正在播放、暂停*/
    final private static int NOPLAY = 1;
    final private static int PLAYING = 2;
    final private static int PAUSED = 3;
    /*当前播放歌曲编号*/
    int ListNum = 0;
    /*播放状态，初始化为未播放*/
    int PLAYSTATE = NOPLAY;
    private Context context;
    private RemoteViews remoteViews;
    private Notification notify;
    private NotificationManager manager;
    private  Runnable tongzhilan;
    private List<NetworkData> networListData;
    private MediaPlayer mediaPlayer;
    private List<MusicData> musicListData;
    public IBinder binder = new MusicBinder();
    private ReadBroadCastReceiver mbcr;
    private RejiBroadCastReceiver Reji;
    private String ReadServiceMsg = "ReadServiceMsg", MainMsg = "MainMsg";
    private Handler mTimeHandler,myhandler;
    private int dantime;
    private TextView tv_startTime;
    private SeekBar seekBar;
    private int type=0;

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
         /*播放结束自动下一曲*/
        if (type==0){
            Log.d("播放完毕:", musicListData.get(ListNum).getDisName());
        }else {
            Log.d("播放完毕:", networListData.get(ListNum).getFilename());
        }

        PLAYSTATE = NOPLAY;
        PlayNext(ListNum);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        SendBroadCastReceiver(MainMsg, "isPlay",
                type==0?musicListData.get(ListNum).getDisName():networListData.get(ListNum).getFilename(),
                1, mediaPlayer.getDuration());
        seekBar.setMax(mediaPlayer.getDuration());
        //设置通知栏
        tongzhi();
        //更新播放时间

      getMusicCurrentPosition();
    }

    //服务绑定类
    public class MusicBinder extends Binder {
        public MusicPlayService getService() {
            return MusicPlayService.this;

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (mbcr != null) {
            unregisterReceiver(mbcr);
        }
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer=null;
        }
        if (Reji!=null){
           unregisterReceiver(Reji);
        }
        unregisterReceiver(headsetPlugReceiver);
        //取消通知栏
        if (remoteViews != null) {
            manager.cancel(100);
            Log.d("服务移除", "ok");

        }
        super.onDestroy();
    }

    /*设置播放状态*/
    void setPlayingUriAudioPlayer(boolean played) {
        tongzhi();
        if (!played) {
            mediaPlayer.pause();
            SendBroadCastReceiver(MainMsg, "isPlay", "", 0, 0);
        } else {
            mediaPlayer.start();

                remoteViews.setImageViewBitmap(R.id.play, BitmapFactory
                        .decodeResource(getResources(),android.R.drawable.ic_media_pause));
            SendBroadCastReceiver(MainMsg, "isPlay",
                    type==0?musicListData.get(ListNum).getDisName():networListData.get(ListNum).getFilename(),
                    1, mediaPlayer.getDuration());

        }

    }

    //播放歌曲函数
    private void LoadMUS(String path) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(MusicPlayService.this);
            //mPlayer.start();
            mediaPlayer.setOnBufferingUpdateListener(this);
            //jiemian()
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    /*播放结束自动下一曲*/
//
//                    Log.d("播放完毕:",musicListData.get(ListNum).getDisName());
//                    PLAYSTATE=NOPLAY;
//                    PlayNext(ListNum);
//                }
//            });
        }
        //mlujin
        //重置播放器

        mediaPlayer.reset();
        //设置播放路
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mbcr = new ReadBroadCastReceiver();
        IntentFilter filter = new IntentFilter(ReadServiceMsg);
        registerReceiver(mbcr, filter);
        System.out.println("音乐服务广播接收器被创建....");
        Reji=new RejiBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(Reji, intentFilter);
        registerHeadsetPlugReceiver();
        //通知栏
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * 播放歌曲
     */
    public void PlayMUS(String path) {
	/*不同状态，执行不同*/
        switch (PLAYSTATE) {
            case NOPLAY:
                LoadMUS(path);
                PLAYSTATE = PLAYING;
                break;
            case PLAYING:
                PlayStop();
                LoadMUS(path);
                break;
            case PAUSED:
                PlayStop();
                LoadMUS(path);
                PLAYSTATE = PLAYING;
                break;
        }
        if (type==0) {
            Log.d("播放： ", musicListData.get(ListNum).getDisName());
        }else {
            Log.d("播放： ", networListData.get(ListNum).getFilename());
        }
    }

    /*播放下一曲*/
    public void PlayNext(int Num) {
        Num += 1;
        if (type==0){
            if (Num > musicListData.size() - 1) {
                Num = 0;
            }
        }else {
            if (Num > networListData.size() - 1) {
                Num = 0;
            }
        }
        ListNum = Num;
        SendBroadCastReceiver(MainMsg, "ListNum", "", ListNum, 0);
        if (type==0){
            PlayMUS(musicListData.get(Num).getUrl());
        }else  {
            GetNetworkUrl(Num);

        }


        if (type==0) {
            Log.d("下一曲：", musicListData.get(Num).getDisName());
        }else {
            Log.d("下一曲：", networListData.get(Num).getFilename());
        }
    }
    public void Play(int Num,int type){
        this.type=type;
        ListNum=Num;
//        if (musicListData==null)
//            Log.d("musicListData","musicListData==null");
        SendBroadCastReceiver(MainMsg, "ListNum", "", ListNum, 0);
        if (this.type==0) {
            PlayMUS(musicListData.get(Num).getUrl());
        }else {
            GetNetworkUrl(Num);
        }
    }

    /*播放上一曲*/
    public void PlayUpper(int Num) {
        Num -= 1;
        if (type==0){
            if (Num <= 0) {
                Num = musicListData.size()-1;

            }
            Log.d("上一曲：", musicListData.get(Num).getDisName());
        }else {
            if (Num <= 0) {
                Num = networListData.size()-1;
            }
            Log.d("上一曲：", networListData.get(Num).getFilename());
        }
        ListNum = Num;
        SendBroadCastReceiver(MainMsg, "ListNum", "", ListNum, 0);
        if (type==0) {
            PlayMUS(musicListData.get(Num).getUrl());
        }else {
            GetNetworkUrl(Num);
        }
    //    Log.d("上一曲：", musicListData.get(Num).getDisName());
    }


    /*暂停歌曲*/
    public void PauseMUS() {
        switch (PLAYSTATE) {
            case PLAYING:
                setPlayingUriAudioPlayer(false);
                if (type==0){
                    Log.d("暂停播放", musicListData.get(ListNum).getDisName());
                }else {
                    Log.d("暂停播放", networListData.get(ListNum).getFilename());
                }

                PLAYSTATE = PAUSED;
                break;
            case PAUSED:
                setPlayingUriAudioPlayer(true);

                if (type==0){
                    Log.d("恢复播放", musicListData.get(ListNum).getDisName());
                }else {
                    Log.d("恢复播放", networListData.get(ListNum).getFilename());
                }
                PLAYSTATE = PLAYING;
                break;
            case NOPLAY:
                ListNum = 0;
                // ListNum-=1;
                if (type==0){
                    PlayMUS(musicListData.get(ListNum).getUrl());
                }else {
                    GetNetworkUrl(ListNum);
                }
                PLAYSTATE = PLAYING;
                break;
        }
    }

    /*停止，关闭声音*/
    public void PlayStop() {
        if (mediaPlayer!=null)
        mediaPlayer.stop();
        // mediaPlayer=null;

    }

    public void SeekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    public void setMusicListData(Context context,List<MusicData> musicListData,TextView tv_startTime ,SeekBar seekBar) {
        this.context=context;
        this.musicListData = musicListData;
        this.tv_startTime=tv_startTime;
        this.seekBar=seekBar;

    }
    public void setMusicListData(List<MusicData> musicListData){
        this.musicListData = musicListData;
    }
    public void setNetworListData(List<NetworkData> networListData){
        this.networListData = networListData;
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) {
            return true;
        }
        return mediaPlayer.isPlaying();
    }

    //广播发送器
    private void SendBroadCastReceiver(String readwho, String msgStr, String obj1, int obj2, int obj3) {
        Intent intent = new Intent();
        intent.putExtra("msg", msgStr);
        intent.putExtra("obj", obj1);
        intent.putExtra("int", obj2);
        intent.putExtra("int2", obj3);
        intent.setAction(readwho);
        sendBroadcast(intent);
    }

    //获取当前播放进度
    public void getMusicCurrentPosition() {
        mTimeHandler = new Handler() {

            public void handleMessage(android.os.Message msg) {

                if (msg.what == 0) {
                    if (mediaPlayer==null)
                        return;
                    dantime = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(dantime);
                    tv_startTime.setText(Timezh.getDisTimeLong(dantime));
                    sendEmptyMessageDelayed(0, 1000);
                }
                //通知栏


            }
        }; //在你的onCreate的类似的方法里面启动这个Handler就可以了： mTimeHandler.sendEmptyMessageDelayed(0, 1000);
       // return dantime;
        mTimeHandler.sendEmptyMessageDelayed(0, 1000);
    }

    //广播接收器
    private class ReadBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("msg")){
                case "ListNum":
                    ListNum=intent.getIntExtra("int",ListNum);
                    break;
                case "last":
                    PlayUpper(ListNum);
                    break;
                case "next":
                    PlayNext(ListNum);
                    break;
                case "play":
                    PauseMUS();
                    break;
                case "urlok": {
                    String url320 = (networListData.get(ListNum).getUrl320());
                    String ur128 = (networListData.get(ListNum).getUrl128());

                    if (url320 != null&&url320.contains("http")) {
                        PlayMUS(url320);
                    } else if (ur128 != null&&ur128.contains("http")) {
                        PlayMUS(ur128);
                    } else {
                        Toast.makeText(context,"this song not paly,play next",Toast.LENGTH_SHORT).show();
                        PlayNext(ListNum);
                    }
                    break;
                }

            }
        }
    }
    //注册耳机广播
    private class RejiBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                        PauseMUS();
                    }

                } else if (intent.getIntExtra("state", 0) == 1) {
                    if ((PLAYSTATE!=PLAYING&&musicListData!=null&&musicListData.size()!=0)
                            ||(PLAYSTATE!=PLAYING&&networListData!=null&&networListData.size()!=0)){
                        PauseMUS();
                    }
                }
            }
        }
    }
//实时监听耳机拔出广播
private void registerHeadsetPlugReceiver() {
    IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    registerReceiver(headsetPlugReceiver, intentFilter);
}
private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                    PauseMUS();
                }
            }
        }

    };
    /**
     * 设置通知
     */
    private void setNotification() {

        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //以上inten能跳转回上一个activity

        //Intent intent = new Intent(this, MainActivity.class);
        // 点击跳转到主界面
        PendingIntent intent_go = PendingIntent.getActivity(this, 5, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notice, intent_go);

        //退出
        Intent myexit = new Intent();
        myexit.putExtra("msg", "exit");
        myexit.setAction(MainMsg);
        PendingIntent myexit_cole = PendingIntent.getBroadcast(this, 4,myexit,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.exit, myexit_cole);

        // 设置上一曲
        Intent last = new Intent();
        last.putExtra("msg", "last");
        last.setAction(ReadServiceMsg);
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, last,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.last, intent_prev);

//        // 设置播放
//        if (PLAYSTATE==PLAYING) {
            Intent playorpause = new Intent();
            playorpause.putExtra("msg", "play");
            playorpause.setAction(ReadServiceMsg);
            PendingIntent intent_play = PendingIntent.getBroadcast(this, 2,
                    playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.play, intent_play);
//        }
//        if (PLAYSTATE!=PLAYING) {
//            Intent playorpause = new Intent();
//            playorpause.putExtra("msg", "play");
//            playorpause.setAction(ReadServiceMsg);
//            PendingIntent intent_play = PendingIntent.getBroadcast(this, 6,
//                    playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.play, intent_play);
//        }

        // 下一曲
        Intent next = new Intent();
        next.putExtra("msg", "next");
        next.setAction(ReadServiceMsg);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, next,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.next, intent_next);

        builder.setSmallIcon(R.drawable.music_tile);
        // 设置顶部图标
        notify = builder.build();
        notify.contentView = remoteViews; // 设置下拉图标
        notify.bigContentView = remoteViews; // 防止显示不完全,需要添加apisupport
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notify.icon = R.drawable.music_tile;
        manager.notify(1, notify);
        startForeground(1, notify);
        /**
         * test
         */

    }
    //更新通知
    private void tongzhi() {
        // TODO Auto-generated method stub
        if (myhandler==null){
            myhandler=new Handler();
        tongzhilan = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // TODO Auto-generated metho stub


                remoteViews = new RemoteViews(getPackageName(),
                        R.layout.customnotice);
                if (type==0){
                    if (musicListData.get(ListNum).getBnendi_pic() != null) {
                        try {
                            remoteViews.setImageViewBitmap(R.id.widget_album, musicListData.get(ListNum).getBnendi_pic());
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    } else {
                        remoteViews.setImageViewResource(R.id.widget_album, R.drawable.lab);
                    }
                }else {
                    if (networListData.get(ListNum).getPic() != null) {
                        try {
                            remoteViews.setImageViewBitmap(R.id.widget_album, networListData.get(ListNum).getPic());
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    } else {
                        remoteViews.setImageViewResource(R.id.widget_album, R.drawable.lab);
                    }
                }
                remoteViews.setImageViewBitmap(R.id.last, BitmapFactory
                        .decodeResource(getResources(), android.R.drawable.ic_media_previous));
                remoteViews.setImageViewBitmap(R.id.next, BitmapFactory
                        .decodeResource(getResources(), android.R.drawable.ic_media_next));
                remoteViews.setImageViewBitmap(R.id.exit, BitmapFactory
                        .decodeResource(getResources(), android.R.drawable.ic_menu_close_clear_cancel));
                if (type==0){
                    remoteViews.setTextViewText(R.id.title, musicListData.get(ListNum).getDisName());
                }else {
                    remoteViews.setTextViewText(R.id.title, networListData.get(ListNum).getFilename());
                }

                // remoteViews.setTextViewText(R.id.widget_artist, info.getArtist());
                if (PLAYSTATE == PLAYING) {
                    remoteViews.setImageViewBitmap(R.id.play, BitmapFactory
                            .decodeResource(getResources(), android.R.drawable.ic_media_pause));
                } else {
                    remoteViews.setImageViewBitmap(R.id.play, BitmapFactory
                            .decodeResource(getResources(), android.R.drawable.ic_media_play));
                }
                setNotification();
            }
        };
    }
        myhandler.post(tongzhilan);//设置通知栏
    }
    public void GetNetworkUrl(int num){
        if (NetworkUtils.isNetworkAvailable(context)){
        GetSongUrl getSongUrl=new GetSongUrl(context,networListData.get(num));
        getSongUrl.execute();
        }else {
            Toast.makeText(context,"network is fail",Toast.LENGTH_SHORT).show();
        }

    }
    public int getType(){
        return type;
    }
}
