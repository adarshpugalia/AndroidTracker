package adarsh.awesomeapps.androidtracker;

import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
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
				SharedPreferences prefs = getSharedPreferences("CONTACTS_preferences", Context.MODE_PRIVATE);
				int count = prefs.getInt("CONTACTS", 0);
				
				for(int i=0; i<count; i++)
				{
					String currentContact = prefs.getString("CONTACTS_"+String.valueOf(i), "");
					if((!currentContact.equals("")) && (!trackedContactNumbers.contains(currentContact)))
						trackedContactNumbers.add(currentContact);
				}
				
				prefs = getSharedPreferences("TRACKING_preferences", Context.MODE_PRIVATE);
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
							editor.commit();
						}
					}
					/*else
					{
						Toast.makeText(getApplicationContext(), "Location not found.", Toast.LENGTH_LONG).show();
					}*/
				}
				
				/* scheduling the runnable again. */
				handler.postDelayed(runnable, 10000);
			}
		};
		handler.postDelayed(runnable, 10000);
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
