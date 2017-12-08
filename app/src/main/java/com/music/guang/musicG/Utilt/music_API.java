package com.music.guang.musicG.Utilt;

public interface music_API {
	static String musicapi1="http://tingapi.ting.baidu.com//v1/restserver/ting?from=android&version=5.9.0.0&channel=webapphomebanner&operator=1&method=baidu.ting.search.merge&format=xml&query="; 
	static String Kgmusic="http://mobilecdn.kugou.com/api/v3/search/song?=1&version=8352&plat=0&sver=5&pagesize=20&keyword=";
	static String KgmusicDownload="http://trackercdn.kugou.com/i/?cmd=4&pid=1&forceDown=0&vip=1";
	static String Kglyc="http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=";
	static String Kglycdownload="http://lyrics.kugou.com/download?ver=1&client=pc&fmt=lrc&charset=utf8";
	static String KgmusicInfo="http://m.kugou.com/app/i/getSongInfo.php?&cmd=playInfo&hash=";
}
