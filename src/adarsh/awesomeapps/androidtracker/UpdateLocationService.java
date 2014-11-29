package adarsh.awesomeapps.androidtracker;

import java.util.concurrent.ExecutionException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateLocationService extends Service
{
	/* handler and runnable for updating location at regular intervals. */
	Handler handler;
	Runnable runnable;
	
	@Override
	public void onCreate()
	{
		
		handler = new Handler();
		runnable = new Runnable() {
			
			@Override
			public void run()
			{
				/* getting the phone number from the shared preferences. */
				SharedPreferences prefs = getSharedPreferences("LOGIN_preferences", Context.MODE_PRIVATE);
				String phone = prefs.getString("PHONE", "");
				
				/* getting the current location. */
				LocationTracker locationTracker = new LocationTracker(getApplicationContext());
				Location location = locationTracker.getLocation();
				
				if(location!=null)
				{
					/* getting the latitude and location. */
					String latitude = String.valueOf(location.getLatitude());
					String longitude = String.valueOf(location.getLongitude());
					
					/* sending the location to the server. */
					ServerRequest serverRequest = new ServerRequest(getApplicationContext());
					try
					{
						serverRequest.execute("update_location.php", String.valueOf(3), "Phone", phone, "Latitude", latitude, "Longitude", longitude).get();
					} catch (InterruptedException | ExecutionException e)
					{
					e.printStackTrace();
					}
					
					/* obtaining the reply from the server. */
					String reply = serverRequest.getReply();
					if(!reply.equals("Success!"))
						Toast.makeText(getApplicationContext(), "Update: "+reply, Toast.LENGTH_SHORT).show();
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "Location Null.", Toast.LENGTH_SHORT).show();
				}
				
				/* scheduling the runnable again. */
				handler.postDelayed(runnable, 5000);
			}
		};
		handler.postDelayed(runnable, 5000);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int contactNumber)
	{
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
