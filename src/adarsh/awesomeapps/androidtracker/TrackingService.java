package adarsh.awesomeapps.androidtracker;

import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class TrackingService extends Service
{
	Vector <String> trackedContactNumbers;
	Handler handler;
	Runnable runnable;
	Timer timer;
	
	@Override
	public void onCreate()
	{
		trackedContactNumbers = new Vector<String>();
		handler = new Handler();
		runnable = new Runnable() {
			
			@Override
			public void run()
			{	
				SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				
				int size = trackedContactNumbers.size();
				for(int i=0; i<size; i++)
				{
					/* sending the location to the server. */
					ServerRequest serverRequest = new ServerRequest(getApplicationContext());
					try
					{
						serverRequest.execute("track_user.php", String.valueOf(1), "Phone", trackedContactNumbers.get(i)).get();
					} catch (InterruptedException | ExecutionException e)
					{
					e.printStackTrace();
					}
					
					/* obtaining the reply from the server. */
					String reply = serverRequest.getReply();
					
					String[] trackedUser = reply.split("\\$");
					if(trackedUser[0].equals("Success!"))
					{
						if(trackedUser.length>1)
						{
							editor.putString(trackedContactNumbers.get(i)+"latitude", trackedUser[1]);
							editor.putString(trackedContactNumbers.get(i)+"longitude", trackedUser[2]);	
							checkGeofence(trackedContactNumbers.get(i), trackedUser[1], trackedUser[2]);
							editor.commit();
						}
					}
					/*else
					{
						Toast.makeText(getApplicationContext(), "Location not found.", Toast.LENGTH_LONG).show();
					}*/
				}
				
				/* scheduling the runnable again. */
				handler.postDelayed(runnable, 30000);
			}
		};
		handler.postDelayed(runnable, 30000);
	}
	
	public void checkGeofence(String contact, String lattitude, String longitude)
	{
		LocationTracker locationTracker = new LocationTracker(getApplicationContext());
        Location location = locationTracker.getLocation(); 
		
		double source_lat = location.getLatitude();
		double source_lon = location.getLongitude();
		
		double dest_lat = Double.parseDouble(lattitude);
		double dest_lon = Double.parseDouble(longitude);
		
		int R=6371;
		double dLat,dLon;
		dLat = Math.toRadians(dest_lat-source_lat);
		dLon = Math.toRadians(dest_lon-source_lon);
		double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLon / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(source_lat)) * Math.cos(Math.toRadians(dest_lat));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = R*c*1000;
	    
	    SharedPreferences prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
	    double radius = prefs.getInt(contact+"radius", 1000);
	    
	    if(dist>radius)
	    	sendNotification(contact);
	}
	
	public void sendNotification(String contact)
	{
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("Geofencing boundary crossed!")
			    .setContentText((new ContactDetails(getContentResolver()).getContactName(contact)) + " has crossed the specified boundary!");
		
		Intent resultIntent = new Intent(this, Home.class);
		
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
	
	@Override
	public int onStartCommand(Intent intent, int flags, int contactNumber)
	{
		String contact = intent.getStringExtra(User.EXTRA_MESSAGE);
		if(trackedContactNumbers.contains(contact))
		{
			trackedContactNumbers.remove(contact);
			Toast.makeText(getApplicationContext(), "Stopped tracking "+contact+".", Toast.LENGTH_SHORT).show();
		}
		else
		{
			trackedContactNumbers.add(contact);
			Toast.makeText(getApplicationContext(), "Tracking "+contact+".", Toast.LENGTH_SHORT).show();
		}
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
