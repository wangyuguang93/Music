package com.music.guang.music.Utilt;

public class Timezh{
	private static int hh,mm,ss;
	private int zhtime;
	public Timezh() {
		// TODO Auto-generated constructor stub
		//this.zhtime=zhtime;
		//this.zhtime=this.zhtime/1000;
		hh=0;
		mm=0;
		ss=0;
	}
	public static  int HH(int hh1) {
		hh=hh1/1000/3600;
		return hh;
	}
	public static int mm(int mm1) {
		HH(mm1);
		mm=(mm1/1000-hh*3600)/60;
		return mm;
	}
	public static int ss(int ss1) {
		HH(ss1);
		mm(ss1);
		ss=ss1/1000-(hh*3600+mm*60);
		
		return ss;
	}
	public static String getDisTimeLong(int time)
	{
		HH(time);
		mm(time);
		ss(time);
		String h="",m="",s="";
		if (hh==0){
			h="";
		}else {
			h=hh+":";
		}
		if (mm<10)
		{
			m="0"+mm;
		}else {
			m=""+mm;
		}
		if (ss<10)
		{
			s="0"+ss;
		}else {
			s=""+ss;
		}

		return h+m+":"+s;
		
	}
}
