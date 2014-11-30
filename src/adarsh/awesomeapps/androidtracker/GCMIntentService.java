package adarsh.awesomeapps.androidtracker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

public class GCMIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	NotificationCompat.Builder builder;
	
	public GCMIntentService()
	{
		super("GCMIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		String message = intent.getExtras().getString("message");
		
		if(message!=null)
		{
			SharedPreferences prefs = getSharedPreferences("TRACKING_REQUESTS_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			if(prefs.contains("Count"))
			{
				int count = prefs.getInt("Count", 0);
				count++;
				editor.putString(String.valueOf(count), message);
				editor.putInt("Count", count);
				editor.commit();
			}
			else
			{
				editor.putInt("Count", 1);
				editor.putString("1", message);
				editor.commit();
			}
			
			
			{
				sendNotification(message);
			}
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void sendNotification(String message)
	{
		ContactDetails contactDetails = new ContactDetails(getContentResolver());
		String contact = contactDetails.getContactName(message);
		if(contact.equals(""))
			contact = message;
		
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("Track Request!")
			    .setContentText(contact + " wants to track your location!");
		
		Intent resultIntent = new Intent(this, TrackRequest.class);
		
		PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(
			    this,
			    0,
			    resultIntent,
			    PendingIntent.FLAG_UPDATE_CURRENT
			);
		
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, mBuilder.build());
	}
}
