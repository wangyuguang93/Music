package com.music.guang.music.NetworkMusic;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class Net_music_download extends AsyncTask<String, Integer, String> {
	private URL url;
	private String extname;
	final int DOWN_THREAD_NUM = 5;
	private Context context;
	String OUT_FILE_NAME = "";
	private int jishu = 0;
	private int errorjishu=0;
	private File songdir;//路径及文件名
	private File songdirtemp;//临时路径及文件名
	private int cs=0;
	private Thread[] threads=new Thread[DOWN_THREAD_NUM];
	private Handler downloadfinish = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
				finish();
			}
		}

	};
	
	InputStream[] isArr = new InputStream[DOWN_THREAD_NUM];

	// 构造函数
	public Net_music_download(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			// extname=params[2];
			// if (extname==null) {
			// String s1=mylujin[mymusicIndex];
			extname = params[0].substring(params[0].lastIndexOf('.') + 1);

			// }

			OUT_FILE_NAME = params[1] + "." + extname;
			url = new URL(params[0]);
			if (url != null) {
				Log.d("下载链接", "" + url);
				isArr[0] = url.openStream();
				long fileLen = getFileLength(url);
				System.out.println("网络资源的大小" + fileLen);


				// 下载目录
				File lujiu = Environment.getExternalStorageDirectory();
				songdir = new File(lujiu + "/kgmusic/" + "/download/", OUT_FILE_NAME);
				for (int i = 1; i < 10; i++) {
					if (songdir.exists()) {
						songdir = new File(lujiu + "/kgmusic/" + "/download/", params[1]+"("+i+")"+"."+extname);
						
					}
					
				}
				
				songdirtemp = new File(lujiu + "/kgmusic/" + "/download/", OUT_FILE_NAME+".temp");
				RandomAccessFile raf = new RandomAccessFile(songdirtemp, "rwd");
				raf.setLength(fileLen);
				raf.close();
				// 假设三个线程下载
				long blockSize = fileLen / DOWN_THREAD_NUM;
				for (int threadId = 1; threadId <= DOWN_THREAD_NUM; threadId++) {
					long startIndex = (threadId - 1) * blockSize;
					long endIndex = threadId * blockSize - 1;
					if (threadId == DOWN_THREAD_NUM) {
						endIndex = fileLen;
					}
					System.out.println("歌曲下载线程：" + threadId + ",下载：" + startIndex + "--->" + endIndex);
					// 开始下载
					threads[threadId-1]=new DownLoadThread(threadId, startIndex, endIndex, url, songdirtemp);
					threads[threadId-1].start();
;
				}
				System.out.println("文件总长度为：" + fileLen);
			} else {
				System.out.println("请求失败！");
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {

		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	// 定义获取指定网络资源的长度的方法
	public static long getFileLength(URL url) throws Exception {
		long length = 0;
		// 打开该URL对应的URLConnection
		URLConnection con = url.openConnection();
		// 获取连接URL资源的长度
		long size = con.getContentLength();
		length = size;
		return length;
	}

	public class DownLoadThread extends Thread {
		private long threadId;
		private long startIndex;
		private long endIndex;
		private URL murl;
		private File dlFile;
		private long dan;
		/**
		 * 
		 * @param threadId
		 *            线程id
		 * @param startIndex2
		 *            线程下载开始位置
		 * @param endIndex2
		 *            线程下载结束位置
		 * @param string
		 *            线程下载结束文件放置地址
		 */
		public DownLoadThread(long threadId, long startIndex2, long endIndex2, URL murl, File songdir) {
			super();
			this.threadId = threadId;
			this.startIndex = startIndex2;
			this.endIndex = endIndex2;
			this.murl = murl;
			dlFile = songdir;
			dan=startIndex2;
		}

		@Override
		public void run() {
			super.run();
			// URL url;
			// try {
			// url = new URL(path);
			
			try {
				HttpURLConnection connection = (HttpURLConnection) murl.openConnection();
				// 请求服务器下载部分的文件，制定开始的位置，和结束位置
				connection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				connection.setReadTimeout(5000);
				// 从服务器获取的全部数据，返回：200，从服务器获取部分数据，返回：206
				int code = connection.getResponseCode();
				System.out.println("code = " + code);
				InputStream is = connection.getInputStream();
				RandomAccessFile raf = new RandomAccessFile(dlFile, "rwd");
				// 随机写文件的时候，从什么时候开始
				raf.seek(startIndex);
				int len = 0;
				byte[] buff = new byte[1024*1024];
				while ((len = is.read(buff)) != -1) {
					raf.write(buff, 0, len);
					dan=(dan+len);
				}
				
				Message message = new Message();
				message.what = 1;
				downloadfinish.sendMessage(message);
				
				is.close();
				raf.close();
				System.out.println("歌曲下载线程：" + threadId + ",下载完成");
			} catch (Exception e) {
				
//				Message message = new Message();
//				message.what = 1;
//				downloadfinish.sendMessage(message);
//				e.printStackTrace();
				try {
					sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (cs<=200) {
					
					Log.d("下载失败", "歌曲下载线程："+threadId+"正在重试"+"-->从"+dan+" ---到"+endIndex);
					new DownLoadThread(threadId, dan, endIndex, murl, dlFile).start();
					cs++;
				}
				if (cs>200) {
					errorjishu++;
					Message message = new Message();
					message.what = 1;
					downloadfinish.sendMessage(message);
				}
				
			}
		}
	}
	/**
	 * 下载情况
	 */
	public void finish() {

		jishu++;
		if (jishu == DOWN_THREAD_NUM&&errorjishu==0) {
			Toast.makeText(context, OUT_FILE_NAME + " 下载完成", Toast.LENGTH_SHORT).show();
			File rename=new File(songdirtemp.
					toString());
			rename.renameTo(songdir);
			rename.delete();
			// Intent scanIntent = new
			// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

			// MediaScannerConnection.scanFile(context, new
			// String[]{Environment.getExternalStorageDirectory().getAbsolutePath()
			// + "/kgmusic/download/",OUT_FILE_NAME}, null, null);
			MediaScannerConnection.scanFile(context,
					new String[] { songdir.toString() },
					null, new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {
							Log.i("ExternalStorage", "Scanned " + path + ":");
							Log.i("ExternalStorage", "-> uri=" + uri);
							Intent intent = new Intent();
							intent.putExtra("msg", "updatemusicdate");
							intent.setAction("MainMsg");
							context.sendBroadcast(intent);
						}
					});
		}
		if (jishu==DOWN_THREAD_NUM&&errorjishu!=0) {
			Toast.makeText(context, OUT_FILE_NAME + " 下载失败", Toast.LENGTH_SHORT).show();
			 
		}
		// context.sendBroadcast(scanIntent);
	
	}
	
	
}
