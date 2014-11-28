package adarsh.awesomeapps.androidtracker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.format.Time;

class TrackingTimerTask extends TimerTask
{
	Context context;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	TrackingTimerTask(Context ctx)
	{
		context = ctx;
		prefs = context.getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
		editor = prefs.edit();
	}
	
	public void run()
	{
		Time time = new Time();
		time.setToNow();
		editor.putString("CUR_TIME", Long.toString(time.toMillis(false)));
		editor.commit();
	}
}

public class TrackingService extends Service
{
	Vector <String> trackedContactNumbers;
	Timer timer;
	
	@Override
	public void onCreate()
	{
		timer = new Timer();
		timer.schedule(new TrackingTimerTask(getApplicationContext()), 1000, 1000);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int contactNumber)
	{
		//trackedContactNumbers.add(String.valueOf(contactNumber));
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onDestroy()
	{
		
	}
}
