package adarsh.awesomeapps.androidtracker;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistrationID {
	
	GoogleCloudMessaging gcm;
	
	/* constructor for the object. */
	public GCMRegistrationID(Context context) {
		gcm = GoogleCloudMessaging.getInstance(context);
	}
	
	/* getting the registration ID from preference file. */
	public void getRegistrationID(Context context)
	{
		final SharedPreferences prefs = context.getSharedPreferences("GCM_preferences", Context.MODE_PRIVATE);
		CommonUtilities.REGISTRATION_ID = prefs.getString("REGISTRATION_ID", "");
	}
	
	/* registring with GCM. */
	public void register(Context context)
	{
		final SharedPreferences prefs = context.getSharedPreferences("GCM_preferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("REGISTRATION_ID", "registration_id");
		
		editor.commit();
	}
}
