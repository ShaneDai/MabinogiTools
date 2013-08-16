package com.dowwii.mabinogi.tools;

import java.util.ArrayList;
import java.util.Calendar;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActyTimer extends Activity {
	TaskTimer timer = null;
	Calendar cal = Calendar.getInstance();
	public class Clock {
		int mHour;
		int mMinute;
		public long lastTime = 0;
		public Clock(int hour, int minute) {
			super();
			mHour = hour;
			mMinute = minute;
		}
		public void setNextTime(long now) {
			cal.setTimeInMillis(now);
			int time = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
			int t1 = time % 2160;
			int mh = t1 / 90;
			int mm = (t1 % 90) / 60;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onDestroy() {
		if (timer != null) timer.cancel(true);
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void onStart(View v) {
		try {
			if (timer == null) {
				timer = new TaskTimer();
			} else {
				timer.cancel(true);
				timer = new TaskTimer();
			}
			ViewGroup lst_clock = (ViewGroup) findViewById(R.id.lst_clock);
			int count = lst_clock.getChildCount();
			for (int i = 0;i < count;i++) {
				View pnl_clock = lst_clock.getChildAt(i);
				int hour = Integer.parseInt(((TextView)pnl_clock.findViewById(R.id.edt_hour)).getText().toString());
				int minute = Integer.parseInt(((TextView)pnl_clock.findViewById(R.id.edt_minute)).getText().toString());
				Clock clock = new Clock(hour, minute);
				timer.addClock(clock);
			}
			timer.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public class TaskTimer extends AsyncTask<Void, Integer, Integer> {

		private boolean run = false;
		private boolean running = false;
		private ArrayList<Clock> lst_clock = new ArrayList<Clock>();
		private int timeShift;

		@Override
		protected Integer doInBackground(Void... args) {
			running  = true;
			try {
				while(run) {
					long now = System.currentTimeMillis();
					cal.setTimeInMillis(now);
					int time = (cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)) + timeShift - 70;
					int t1 = time % 2160;
					int mh = (int) (t1 / 90);
					int mm = (int) ((t1 % 90) / 15) * 10;
					publishProgress(mh, mm);  
					Thread.sleep(5000);
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			running = false;
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			long now = System.currentTimeMillis();
			int hour = values[0];
			int minute = values[1];
			((TextView)findViewById(R.id.txt_hour)).setText(String.valueOf(hour));
			((TextView)findViewById(R.id.txt_minute)).setText(String.valueOf(minute));
			boolean beep = false;
			for (int i = 0;i < lst_clock.size();i++) {
				Clock clock = lst_clock.get(i);
				if (clock.mHour == hour && clock.mMinute == minute && now - clock.lastTime > 20000) {
					beep = true;
				}
			}
			if (beep) {
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
			}
		}

		public void addClock(Clock clock) {
			lst_clock.add(clock);
		}

		public void start() {
			run = true;
			if (!running)
				execute();
		}
		
	}

}
